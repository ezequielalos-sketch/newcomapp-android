package com.pilar.newcomapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilar.newcomapp.data.local.entity.PartidoEntity
import com.pilar.newcomapp.data.local.entity.RotacionActualEntity
import com.pilar.newcomapp.data.repository.PartidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartidoViewModel @Inject constructor(
    private val repository: PartidoRepository
) : ViewModel() {

    val partidoActivo: StateFlow<PartidoEntity?> = repository
        .obtenerPartidoActivo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val historialPartidos: StateFlow<List<PartidoEntity>> = repository
        .obtenerTodosLosPartidos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rotacionActual = MutableStateFlow<RotacionActualEntity?>(null)
    val rotacionActual: StateFlow<RotacionActualEntity?> = _rotacionActual

    private val _advertenciaMixto = MutableStateFlow("")
    val advertenciaMixto: StateFlow<String> = _advertenciaMixto

    // Almacen de liberos (nombre y datos guardados aparte de la rotacion)
    private var liberoMNombre: String = ""
    private var liberoFNombre: String = ""
    // Posicion original del jugador que el libero reemplazo
    private var liberoMReemplazo: Int = -1  // indice 0-5
    private var liberoFReemplazo: Int = -1
    private var titularReemplazadoM: String = ""
    private var titularReemplazadoF: String = ""

    init {
        viewModelScope.launch {
            partidoActivo.filterNotNull().collect { partido ->
                repository.obtenerRotacion(partido.id).collect { rotacion ->
                    _rotacionActual.value = rotacion
                    verificarReglaMixto(partido, rotacion)
                }
            }
        }
    }

    private fun verificarReglaMixto(partido: PartidoEntity, rotacion: RotacionActualEntity?) {
        if (partido.modalidad != "Mixto" || rotacion == null) {
            _advertenciaMixto.value = ""
            return
        }
        val sexos = listOf(
            rotacion.sexo1, rotacion.sexo2, rotacion.sexo3,
            rotacion.sexo4, rotacion.sexo5, rotacion.sexo6
        )
        val mujeres = sexos.count { it == "F" }
        _advertenciaMixto.value = if (mujeres < partido.minimoMujeresMixto) {
            "Atencion: solo $mujeres mujer(es) en cancha. Minimo: ${partido.minimoMujeresMixto}"
        } else ""
    }

    fun crearNuevoPartido(
        nombreLocal: String = "Nosotros",
        nombreVisitante: String = "Rival",
        modalidad: String = "Masculino",
        categoria: String = "+40",
        cantidadSets: Int = 3,
        puntajePorSet: Int = 15
    ) {
        viewModelScope.launch {
            val partido = PartidoEntity(
                nombreEquipoLocal = nombreLocal,
                nombreEquipoVisitante = nombreVisitante,
                modalidad = modalidad,
                categoria = categoria,
                cantidadSets = cantidadSets,
                puntajePorSet = puntajePorSet,
                puntajeSetFinal = 10
            )
            val id = repository.crearPartido(partido)
            
            val sexoDefault = if (modalidad == "Femenino") "F" else "M"
            val rotacion = RotacionActualEntity(
                partidoId = id,
                posicion1 = "", posicion2 = "", posicion3 = "",
                posicion4 = "", posicion5 = "", posicion6 = "",
                sexo1 = sexoDefault, sexo2 = sexoDefault, sexo3 = sexoDefault,
                sexo4 = sexoDefault, sexo5 = sexoDefault, sexo6 = sexoDefault
            )
            repository.guardarRotacion(rotacion)
            
            // Reset liberos
            liberoMNombre = ""
            liberoFNombre = ""
            liberoMReemplazo = -1
            liberoFReemplazo = -1
            titularReemplazadoM = ""
            titularReemplazadoF = ""
        }
    }

    fun actualizarConfiguracionPartido(
        modalidad: String,
        categoria: String,
        cantidadSets: Int,
        puntajePorSet: Int
    ) {
        val p = partidoActivo.value ?: return
        viewModelScope.launch {
            val puntajeSetFinal = when {
                puntajePorSet == 15 -> 10
                puntajePorSet == 21 -> 15
                else -> puntajePorSet
            }
            repository.actualizarPartido(
                p.copy(
                    modalidad = modalidad,
                    categoria = categoria,
                    cantidadSets = cantidadSets,
                    puntajePorSet = puntajePorSet,
                    puntajeSetFinal = puntajeSetFinal
                )
            )
        }
    }

    fun actualizarNombresEquipos(nombreLocal: String, nombreVisitante: String) {
        val p = partidoActivo.value ?: return
        viewModelScope.launch {
            repository.actualizarPartido(
                p.copy(
                    nombreEquipoLocal = nombreLocal,
                    nombreEquipoVisitante = nombreVisitante
                )
            )
        }
    }

    /**
     * Guarda los nombres de los liberos para usarlos cuando se ingresan a cancha.
     */
    fun guardarNombresLiberos(nombreM: String, nombreF: String) {
        liberoMNombre = nombreM
        liberoFNombre = nombreF
    }

    /**
     * Obtiene los nombres actuales de los liberos.
     */
    fun obtenerNombresLiberos(): Pair<String, String> {
        return Pair(liberoMNombre, liberoFNombre)
    }

    /**
     * Ingresa el libero del sexo indicado en la primera posicion defensiva
     * disponible (P1, P6, P5) de derecha a izquierda.
     * Posiciones defensivas: indice 0 (P1), indice 5 (P6), indice 4 (P5).
     * Si ya esta en cancha, lo saca y restaura al titular.
     */
    fun ingresarLibero(sexo: String) {
        val rotacion = _rotacionActual.value ?: return
        val nombres = mutableListOf(rotacion.posicion1, rotacion.posicion2, rotacion.posicion3, rotacion.posicion4, rotacion.posicion5, rotacion.posicion6)
        val sexos = mutableListOf(rotacion.sexo1, rotacion.sexo2, rotacion.sexo3, rotacion.sexo4, rotacion.sexo5, rotacion.sexo6)
        val liberos = mutableListOf(rotacion.libero1, rotacion.libero2, rotacion.libero3, rotacion.libero4, rotacion.libero5, rotacion.libero6)

        val reemplazoActual = if (sexo == "M") liberoMReemplazo else liberoFReemplazo

        if (reemplazoActual >= 0) {
            // Libero ya esta en cancha -> sacarlo y devolver titular
            val titular = if (sexo == "M") titularReemplazadoM else titularReemplazadoF
            nombres[reemplazoActual] = titular
            liberos[reemplazoActual] = false
            if (sexo == "M") {
                liberoMReemplazo = -1
                titularReemplazadoM = ""
            } else {
                liberoFReemplazo = -1
                titularReemplazadoF = ""
            }
        } else {
            // Buscar nombre del libero: usar el nombre guardado, o poner "Libero M"/"Libero F"
            val nombreLibero = if (sexo == "M") {
                liberoMNombre.ifBlank { "Libero M" }
            } else {
                liberoFNombre.ifBlank { "Libero F" }
            }

            // Posiciones defensivas de derecha a izquierda: P1(indice 0), P6(indice 5), P5(indice 4)
            val posicionesDefensivas = listOf(0, 5, 4)
            var ingresado = false

            for (idx in posicionesDefensivas) {
                // Solo reemplazar jugadores del mismo sexo que no sean libero
                if (sexos[idx] == sexo && !liberos[idx]) {
                    if (sexo == "M") {
                        titularReemplazadoM = nombres[idx]
                        liberoMReemplazo = idx
                    } else {
                        titularReemplazadoF = nombres[idx]
                        liberoFReemplazo = idx
                    }
                    nombres[idx] = nombreLibero
                    liberos[idx] = true
                    ingresado = true
                    break
                }
            }

            if (!ingresado) return // No hay posicion defensiva disponible
        }

        val nueva = rotacion.copy(
            posicion1 = nombres[0], posicion2 = nombres[1], posicion3 = nombres[2],
            posicion4 = nombres[3], posicion5 = nombres[4], posicion6 = nombres[5],
            libero1 = liberos[0], libero2 = liberos[1], libero3 = liberos[2],
            libero4 = liberos[3], libero5 = liberos[4], libero6 = liberos[5]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun sumarPuntoLocal() {
        val p = partidoActivo.value ?: return
        if (p.finalizado) return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosLocal = p.puntosLocal + 1))
            verificarFinDeSet()
        }
    }

    fun sumarPuntoVisitante() {
        val p = partidoActivo.value ?: return
        if (p.finalizado) return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosVisitante = p.puntosVisitante + 1))
            verificarFinDeSet()
        }
    }

    private suspend fun verificarFinDeSet() {
        val p = repository.obtenerPartidoActivo().first() ?: return
        
        val esSetFinal = p.setActual >= p.cantidadSets
        val limite = if (!esSetFinal) p.puntajePorSet else p.puntajeSetFinal
        val topeMax = if (!esSetFinal) {
            if (p.puntajePorSet == 15) 17 else p.puntajePorSet + 2
        } else {
            if (p.puntajeSetFinal == 10) 12 else p.puntajeSetFinal + 2
        }
        
        val puntosL = p.puntosLocal
        val puntosV = p.puntosVisitante

        val localGanaSet = (puntosL >= limite && (puntosL - puntosV) >= 2) || puntosL >= topeMax
        val visitanteGanaSet = (puntosV >= limite && (puntosV - puntosL) >= 2) || puntosV >= topeMax

        if (localGanaSet || visitanteGanaSet) {
            finalizarSetInternal(p, localGanaSet)
        }
    }

    fun finalizarSet() {
        val p = partidoActivo.value ?: return
        val localGanaSet = p.puntosLocal > p.puntosVisitante
        viewModelScope.launch {
            finalizarSetInternal(p, localGanaSet)
        }
    }

    private suspend fun finalizarSetInternal(p: PartidoEntity, localGanaSet: Boolean) {
        val sL = if (localGanaSet) p.setsLocal + 1 else p.setsLocal
        val sV = if (!localGanaSet) p.setsVisitante + 1 else p.setsVisitante
        
        var nP = when (p.setActual) {
            1 -> p.copy(set1Local = p.puntosLocal, set1Visitante = p.puntosVisitante)
            2 -> p.copy(set2Local = p.puntosLocal, set2Visitante = p.puntosVisitante)
            3 -> p.copy(set3Local = p.puntosLocal, set3Visitante = p.puntosVisitante)
            else -> p
        }

        nP = nP.copy(
            setsLocal = sL,
            setsVisitante = sV,
            puntosLocal = 0,
            puntosVisitante = 0,
            setActual = p.setActual + 1
        )

        // Para 1 set: gana el primero en llegar a 1 set
        // Para 3 sets: gana el primero en llegar a 2 sets
        val setsNecesarios = if (p.cantidadSets == 1) 1 else 2
        if (sL >= setsNecesarios || sV >= setsNecesarios) {
            nP = nP.copy(finalizado = true)
        }

        repository.actualizarPartido(nP)
    }

    fun restarPuntoLocal() {
        val p = partidoActivo.value ?: return
        if (p.puntosLocal <= 0) return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosLocal = p.puntosLocal - 1))
        }
    }

    fun restarPuntoVisitante() {
        val p = partidoActivo.value ?: return
        if (p.puntosVisitante <= 0) return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosVisitante = p.puntosVisitante - 1))
        }
    }

    fun rotarSiguiente() {
        val rotacion = _rotacionActual.value ?: return
        val j = mutableListOf(rotacion.posicion1, rotacion.posicion2, rotacion.posicion3, rotacion.posicion4, rotacion.posicion5, rotacion.posicion6)
        val s = mutableListOf(rotacion.sexo1, rotacion.sexo2, rotacion.sexo3, rotacion.sexo4, rotacion.sexo5, rotacion.sexo6)
        val l = mutableListOf(rotacion.libero1, rotacion.libero2, rotacion.libero3, rotacion.libero4, rotacion.libero5, rotacion.libero6)
        
        // Antes de rotar, si hay libero en posicion defensiva que pasa a ataque, sacar
        sacarLiberoSiPasaAAtaque(j, s, l)

        val nueva = rotacion.copy(
            posicion1 = j[1], posicion2 = j[2], posicion3 = j[3], posicion4 = j[4], posicion5 = j[5], posicion6 = j[0],
            sexo1 = s[1], sexo2 = s[2], sexo3 = s[3], sexo4 = s[4], sexo5 = s[5], sexo6 = s[0],
            libero1 = l[1], libero2 = l[2], libero3 = l[3], libero4 = l[4], libero5 = l[5], libero6 = l[0]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun rotarAnterior() {
        val rotacion = _rotacionActual.value ?: return
        val j = mutableListOf(rotacion.posicion1, rotacion.posicion2, rotacion.posicion3, rotacion.posicion4, rotacion.posicion5, rotacion.posicion6)
        val s = mutableListOf(rotacion.sexo1, rotacion.sexo2, rotacion.sexo3, rotacion.sexo4, rotacion.sexo5, rotacion.sexo6)
        val l = mutableListOf(rotacion.libero1, rotacion.libero2, rotacion.libero3, rotacion.libero4, rotacion.libero5, rotacion.libero6)
        
        sacarLiberoSiPasaAAtaque(j, s, l)

        val nueva = rotacion.copy(
            posicion1 = j[5], posicion2 = j[0], posicion3 = j[1], posicion4 = j[2], posicion5 = j[3], posicion6 = j[4],
            sexo1 = s[5], sexo2 = s[0], sexo3 = s[1], sexo4 = s[2], sexo5 = s[3], sexo6 = s[4],
            libero1 = l[5], libero2 = l[0], libero3 = l[1], libero4 = l[2], libero5 = l[3], libero6 = l[4]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    /**
     * Antes de rotar, verifica si un libero esta en una posicion defensiva
     * que al rotar pasaria a posicion de ataque. Si es asi, lo saca y
     * restaura al titular original.
     */
    private fun sacarLiberoSiPasaAAtaque(
        nombres: MutableList<String>,
        sexos: MutableList<String>,
        liberos: MutableList<Boolean>
    ) {
        for (idx in listOf(0, 4, 5)) {
            if (liberos[idx]) {
                val sexoLib = sexos[idx]
                val titular = if (sexoLib == "M") titularReemplazadoM else titularReemplazadoF
                if (titular.isNotBlank()) {
                    nombres[idx] = titular
                    liberos[idx] = false
                    if (sexoLib == "M") {
                        liberoMReemplazo = -1
                        titularReemplazadoM = ""
                    } else {
                        liberoFReemplazo = -1
                        titularReemplazadoF = ""
                    }
                }
            }
        }
    }

    fun guardarNombresJugadores(nombres: List<String>, sexos: List<String>, liberos: List<Boolean>) {
        val rotacion = _rotacionActual.value ?: return
        val nueva = rotacion.copy(
            posicion1 = nombres.getOrElse(0) { rotacion.posicion1 },
            posicion2 = nombres.getOrElse(1) { rotacion.posicion2 },
            posicion3 = nombres.getOrElse(2) { rotacion.posicion3 },
            posicion4 = nombres.getOrElse(3) { rotacion.posicion4 },
            posicion5 = nombres.getOrElse(4) { rotacion.posicion5 },
            posicion6 = nombres.getOrElse(5) { rotacion.posicion6 },
            sexo1 = sexos.getOrElse(0) { rotacion.sexo1 },
            sexo2 = sexos.getOrElse(1) { rotacion.sexo2 },
            sexo3 = sexos.getOrElse(2) { rotacion.sexo3 },
            sexo4 = sexos.getOrElse(3) { rotacion.sexo4 },
            sexo5 = sexos.getOrElse(4) { rotacion.sexo5 },
            sexo6 = sexos.getOrElse(5) { rotacion.sexo6 },
            libero1 = liberos.getOrElse(0) { rotacion.libero1 },
            libero2 = liberos.getOrElse(1) { rotacion.libero2 },
            libero3 = liberos.getOrElse(2) { rotacion.libero3 },
            libero4 = liberos.getOrElse(3) { rotacion.libero4 },
            libero5 = liberos.getOrElse(4) { rotacion.libero5 },
            libero6 = liberos.getOrElse(5) { rotacion.libero6 }
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }
}
