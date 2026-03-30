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
    val finalizado: Boolean = false
)
