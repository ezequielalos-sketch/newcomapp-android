package com.pilar.newcomapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rotacion_actual")
data class RotacionActualEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partidoId: Long,
    val posicion1: String = "",
    val posicion2: String = "",
    val posicion3: String = "",
    val posicion4: String = "",
    val posicion5: String = "",
    val posicion6: String = ""
)
