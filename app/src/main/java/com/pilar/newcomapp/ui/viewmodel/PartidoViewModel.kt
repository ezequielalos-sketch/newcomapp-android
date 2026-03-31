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

    init {
        viewModelScope.launch {
            partidoActivo.filterNotNull().collect { partido ->
                repository.obtenerRotacion(partido.id).collect { rotacion ->
                    _rotacionActual.value = rotacion
                }
            }
        }
    }

    fun crearNuevoPartido(nombreLocal: String = "Nosotros", nombreVisitante: String = "Rival") {
        viewModelScope.launch {
            val partido = PartidoEntity(
                nombreEquipoLocal = nombreLocal,
                nombreEquipoVisitante = nombreVisitante
            )
            val id = repository.crearPartido(partido)
            val rotacion = RotacionActualEntity(
                partidoId = id,
                posicion1 = "", posicion2 = "", posicion3 = "",
                posicion4 = "", posicion5 = "", posicion6 = ""
            )
            repository.guardarRotacion(rotacion)
        }
    }

    fun sumarPuntoLocal() {
        val partido = partidoActivo.value ?: return
        viewModelScope.launch {
            repository.actualizarPartido(partido.copy(puntosLocal = partido.puntosLocal + 1))
        }
    }

    fun sumarPuntoVisitante() {
        val partido = partidoActivo.value ?: return
        viewModelScope.launch {
            repository.actualizarPartido(partido.copy(puntosVisitante = partido.puntosVisitante + 1))
        }
    }

    fun restarPuntoLocal() {
        val partido = partidoActivo.value ?: return
        if (partido.puntosLocal <= 0) return
        viewModelScope.launch {
            repository.actualizarPartido(partido.copy(puntosLocal = partido.puntosLocal - 1))
        }
    }

    fun restarPuntoVisitante() {
        val partido = partidoActivo.value ?: return
        if (partido.puntosVisitante <= 0) return
        viewModelScope.launch {
            repository.actualizarPartido(partido.copy(puntosVisitante = partido.puntosVisitante - 1))
        }
    }

    fun rotarSiguiente() {
        val rotacion = _rotacionActual.value ?: return
        val j = listOf(
            rotacion.posicion1, rotacion.posicion2, rotacion.posicion3,
            rotacion.posicion4, rotacion.posicion5, rotacion.posicion6
        )
        val nueva = rotacion.copy(
            posicion1 = j[1], posicion2 = j[2], posicion3 = j[3],
            posicion4 = j[4], posicion5 = j[5], posicion6 = j[0]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun rotarAnterior() {
        val rotacion = _rotacionActual.value ?: return
        val j = listOf(
            rotacion.posicion1, rotacion.posicion2, rotacion.posicion3,
            rotacion.posicion4, rotacion.posicion5, rotacion.posicion6
        )
        val nueva = rotacion.copy(
            posicion1 = j[5], posicion2 = j[0], posicion3 = j[1],
            posicion4 = j[2], posicion5 = j[3], posicion6 = j[4]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun actualizarJugadorEnPosicion(posicion: Int, nombre: String) {
        val rotacion = _rotacionActual.value ?: return
        val nueva = when (posicion) {
            1 -> rotacion.copy(posicion1 = nombre)
            2 -> rotacion.copy(posicion2 = nombre)
            3 -> rotacion.copy(posicion3 = nombre)
            4 -> rotacion.copy(posicion4 = nombre)
            5 -> rotacion.copy(posicion5 = nombre)
            6 -> rotacion.copy(posicion6 = nombre)
            else -> rotacion
        }
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun guardarNombresJugadores(nombres: List<String>) {
        val rotacion = _rotacionActual.value ?: return
        val nueva = rotacion.copy(
            posicion1 = nombres.getOrElse(0) { rotacion.posicion1 },
            posicion2 = nombres.getOrElse(1) { rotacion.posicion2 },
            posicion3 = nombres.getOrElse(2) { rotacion.posicion3 },
            posicion4 = nombres.getOrElse(3) { rotacion.posicion4 },
            posicion5 = nombres.getOrElse(4) { rotacion.posicion5 },
            posicion6 = nombres.getOrElse(5) { rotacion.posicion6 }
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nueva)
            _rotacionActual.value = nueva
        }
    }

    fun finalizarSet() {
        val partido = partidoActivo.value ?: return
        val (setsLocal, setsVisitante) = if (partido.puntosLocal > partido.puntosVisitante)
            Pair(partido.setsLocal + 1, partido.setsVisitante)
        else
            Pair(partido.setsLocal, partido.setsVisitante + 1)
        viewModelScope.launch {
            repository.actualizarPartido(
                partido.copy(
                    setsLocal = setsLocal, setsVisitante = setsVisitante,
                    puntosLocal = 0, puntosVisitante = 0,
                    setActual = partido.setActual + 1
                )
            )
        }
    }
}
