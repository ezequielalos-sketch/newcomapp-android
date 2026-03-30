package com.pilar.newcomapp.data.local.dao

import androidx.room.*
import com.pilar.newcomapp.data.local.entity.RotacionActualEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RotacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRotacion(rotacion: RotacionActualEntity): Long

    @Update
    suspend fun actualizarRotacion(rotacion: RotacionActualEntity)

    @Query("SELECT * FROM rotacion_actual WHERE partidoId = :partidoId")
    fun obtenerRotacionPorPartido(partidoId: Long): Flow<RotacionActualEntity?>

    @Query("DELETE FROM rotacion_actual WHERE partidoId = :partidoId")
    suspend fun eliminarRotacionPorPartido(partidoId: Long)
}
