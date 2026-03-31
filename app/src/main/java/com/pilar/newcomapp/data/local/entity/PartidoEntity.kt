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
    // Configuracion del partido
    val modalidad: String = "Masculino", // Masculino, Femenino, Mixto
    val categoria: String = "+40",       // +40, +50, +55, +60, +65, +68, Open
    val cantidadSets: Int = 3,           // 3 o 5
    val puntajePorSet: Int = 25,         // 15, 21, 25
    val puntajeSetFinal: Int = 15,       // puntaje del ultimo set
    val cantidadJugadores: Int = 6,      // 6 en cancha
    val minimoMujeresMixto: Int = 3      // minimo mujeres en cancha en mixto
)
