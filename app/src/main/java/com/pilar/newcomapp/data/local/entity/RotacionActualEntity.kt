package com.pilar.newcomapp.data.local.entity

import androidx.room.*

@Entity(
    tableName = "rotaciones_actuales",
    foreignKeys = [
        ForeignKey(entity = SetEntity::class, parentColumns = ["setId"], childColumns = ["setOwnerId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = JugadorEntity::class, parentColumns = ["jugadorId"], childColumns = ["jugadorId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("setOwnerId"), Index("jugadorId"), Index(value = ["setOwnerId", "lado", "posicion"], unique = true)]
)
data class RotacionActualEntity(
    @PrimaryKey(autoGenerate = true) val rotacionId: Long = 0L,
    val setOwnerId: Long,
    val lado: String,
    val posicion: Int,
    val jugadorId: Long,
    val ordenActual: Int
)
