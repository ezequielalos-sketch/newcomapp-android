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
            cargarRotacion(id)
        }
    }

    fun cargarRotacion(partidoId: Long) {
        viewModelScope.launch {
            repository.obtenerRotacion(partidoId).collect {
                _rotacionActual.value = it
            }
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
        val jugadores = listOf(
            rotacion.posicion1, rotacion.posicion2, rotacion.posicion3,
            rotacion.posicion4, rotacion.posicion5, rotacion.posicion6
        )
        // Rotacion voley: 1->6->5->4->3->2->1
        val nuevaRotacion = rotacion.copy(
            posicion1 = jugadores[1],
            posicion2 = jugadores[2],
            posicion3 = jugadores[3],
            posicion4 = jugadores[4],
            posicion5 = jugadores[5],
            posicion6 = jugadores[0]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nuevaRotacion)
            _rotacionActual.value = nuevaRotacion
        }
    }

    fun rotarAnterior() {
        val rotacion = _rotacionActual.value ?: return
        val jugadores = listOf(
            rotacion.posicion1, rotacion.posicion2, rotacion.posicion3,
            rotacion.posicion4, rotacion.posicion5, rotacion.posicion6
        )
        // Rotacion inversa
        val nuevaRotacion = rotacion.copy(
            posicion1 = jugadores[5],
            posicion2 = jugadores[0],
            posicion3 = jugadores[1],
            posicion4 = jugadores[2],
            posicion5 = jugadores[3],
            posicion6 = jugadores[4]
        )
        viewModelScope.launch {
            repository.actualizarRotacion(nuevaRotacion)
            _rotacionActual.value = nuevaRotacion
        }
    }

    fun actualizarJugadorEnPosicion(posicion: Int, nombre: String) {
        val rotacion = _rotacionActual.value ?: return
        val nuevaRotacion = when (posicion) {
            1 -> rotacion.copy(posicion1 = nombre)
            2 -> rotacion.copy(posicion2 = nombre)
            3 -> rotacion.copy(posicion3 = nombre)
            4 -> rotacion.copy(posicion4 = nombre)
            5 -> rotacion.copy(posicion5 = nombre)
            6 -> rotacion.copy(posicion6 = nombre)
            else -> rotacion
        }
        viewModelScope.launch {
            repository.actualizarRotacion(nuevaRotacion)
            _rotacionActual.value = nuevaRotacion
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
                    setsLocal = setsLocal,
                    setsVisitante = setsVisitante,
                    puntosLocal = 0,
                    puntosVisitante = 0,
                    setActual = partido.setActual + 1
                )
            )
        }
    }
}
