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
        }
    }

    /**
     * Actualiza la configuracion del partido activo (modalidad, categoria, sets, puntaje).
     * Llamada desde ConfigurarJugadoresScreen al guardar.
     */
    fun actualizarConfiguracionPartido(
        modalidad: String,
        categoria: String,
        cantidadSets: Int,
        puntajePorSet: Int
    ) {
        val p = partidoActivo.value ?: return
        viewModelScope.launch {
            val puntajeSetFinal = when {
                puntajePorSet == 15 -> 10  // Newcom: sets 1-2 a 15, set 3 a 10
                puntajePorSet == 25 -> 15  // Volleyball: sets 1-4 a 25, set 5 a 15
                else -> puntajePorSet      // Fallback
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

    fun sumarPuntoLocal() {
        val p = partidoActivo.value ?: return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosLocal = p.puntosLocal + 1))
            verificarFinDeSet()
        }
    }

    fun sumarPuntoVisitante() {
        val p = partidoActivo.value ?: return
        viewModelScope.launch {
            repository.actualizarPartido(p.copy(puntosVisitante = p.puntosVisitante + 1))
            verificarFinDeSet()
        }
    }

    private suspend fun verificarFinDeSet() {
        val p = repository.obtenerPartidoActivo().first() ?: return
        
        val limite = if (p.setActual < p.cantidadSets) p.puntajePorSet else p.puntajeSetFinal
        val topeMax = if (p.setActual < p.cantidadSets) 17 else 12
        
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
        
        // Guardar resultado del set en el historial
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

        // Verificar si termino el partido
        val setsNecesarios = if (p.cantidadSets == 3) 2 else 3
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
        val j = listOf(rotacion.posicion1, rotacion.posicion2, rotacion.posicion3, rotacion.posicion4, rotacion.posicion5, rotacion.posicion6)
        val s = listOf(rotacion.sexo1, rotacion.sexo2, rotacion.sexo3, rotacion.sexo4, rotacion.sexo5, rotacion.sexo6)
        val l = listOf(rotacion.libero1, rotacion.libero2, rotacion.libero3, rotacion.libero4, rotacion.libero5, rotacion.libero6)
        
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
        val j = listOf(rotacion.posicion1, rotacion.posicion2, rotacion.posicion3, rotacion.posicion4, rotacion.posicion5, rotacion.posicion6)
        val s = listOf(rotacion.sexo1, rotacion.sexo2, rotacion.sexo3, rotacion.sexo4, rotacion.sexo5, rotacion.sexo6)
        val l = listOf(rotacion.libero1, rotacion.libero2, rotacion.libero3, rotacion.libero4, rotacion.libero5, rotacion.libero6)
        
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
