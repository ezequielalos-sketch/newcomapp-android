package com.pilar.newcomapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidos")
data class PartidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreEquipoLocal: String = "Nosotros",
    val nombreEquipoVisitante: String = "Rival",
    val puntosLocal: Int = 0,
    val puntosVisitante: Int = 0,
    val setsLocal: Int = 0,
    val setsVisitante: Int = 0,
    val setActual: Int = 1,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val finalizado: Boolean = false,
    // Historial de sets (puntaje al finalizar cada set)
    val set1Local: Int = -1,       // -1 = set no jugado
    val set1Visitante: Int = -1,
    val set2Local: Int = -1,
    val set2Visitante: Int = -1,
    val set3Local: Int = -1,
    val set3Visitante: Int = -1,
    // Configuracion del partido
    val modalidad: String = "Masculino", // Masculino, Femenino, Mixto
    val categoria: String = "+40",       // +40, +50, +55, +60, +65, +68, Open
    val cantidadSets: Int = 3,           // 3 o 5
    val puntajePorSet: Int = 15,         // Newcom: 15 pts sets 1 y 2
    val puntajeSetFinal: Int = 10,       // Newcom: 10 pts set 3 (desempate)
    val cantidadJugadores: Int = 6,      // 6 en cancha
    val minimoMujeresMixto: Int = 3      // minimo mujeres en cancha en mixto
)
