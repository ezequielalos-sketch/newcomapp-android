package com.pilar.newcomapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rotacion_actual")
data class RotacionActualEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partidoId: Long,
    // Nombres
    val posicion1: String = "",
    val posicion2: String = "",
    val posicion3: String = "",
    val posicion4: String = "",
    val posicion5: String = "",
    val posicion6: String = "",
    // Sexo: "M" = Masculino, "F" = Femenino
    val sexo1: String = "M",
    val sexo2: String = "M",
    val sexo3: String = "M",
    val sexo4: String = "M",
    val sexo5: String = "M",
    val sexo6: String = "M",
    // Libero
    val libero1: Boolean = false,
    val libero2: Boolean = false,
    val libero3: Boolean = false,
    val libero4: Boolean = false,
    val libero5: Boolean = false,
    val libero6: Boolean = false
)
