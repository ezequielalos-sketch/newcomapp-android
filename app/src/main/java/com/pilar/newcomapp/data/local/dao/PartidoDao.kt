package com.pilar.newcomapp.data.local.dao

import androidx.room.*
import com.pilar.newcomapp.data.local.entity.PartidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPartido(partido: PartidoEntity): Long

    @Update
    suspend fun actualizarPartido(partido: PartidoEntity)

    @Delete
    suspend fun eliminarPartido(partido: PartidoEntity)

    @Query("SELECT * FROM partidos ORDER BY fechaCreacion DESC")
    fun obtenerTodosLosPartidos(): Flow<List<PartidoEntity>>

    @Query("SELECT * FROM partidos WHERE id = :id")
    fun obtenerPartidoPorId(id: Long): Flow<PartidoEntity?>

    @Query("SELECT * FROM partidos WHERE finalizado = 0 LIMIT 1")
    fun obtenerPartidoActivo(): Flow<PartidoEntity?>
}
