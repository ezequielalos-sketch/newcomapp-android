package com.pilar.newcomapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pilar.newcomapp.data.local.dao.PartidoDao
import com.pilar.newcomapp.data.local.dao.RotacionDao
import com.pilar.newcomapp.data.local.entity.PartidoEntity
import com.pilar.newcomapp.data.local.entity.RotacionActualEntity

@Database(
    entities = [PartidoEntity::class, RotacionActualEntity::class],
    version = 3,
    exportSchema = false
)
abstract class NewcomDatabase : RoomDatabase() {
    abstract fun partidoDao(): PartidoDao
    abstract fun rotacionDao(): RotacionDao
}
