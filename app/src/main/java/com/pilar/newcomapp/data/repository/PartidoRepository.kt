package com.pilar.newcomapp.data.repository

import com.pilar.newcomapp.data.local.dao.PartidoDao
import com.pilar.newcomapp.data.local.dao.RotacionDao
import com.pilar.newcomapp.data.local.entity.PartidoEntity
import com.pilar.newcomapp.data.local.entity.RotacionActualEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartidoRepository @Inject constructor(
    private val partidoDao: PartidoDao,
    private val rotacionDao: RotacionDao
) {
    fun obtenerTodosLosPartidos(): Flow<List<PartidoEntity>> =
        partidoDao.obtenerTodosLosPartidos()

    fun obtenerPartidoActivo(): Flow<PartidoEntity?> =
        partidoDao.obtenerPartidoActivo()

    fun obtenerPartidoPorId(id: Long): Flow<PartidoEntity?> =
        partidoDao.obtenerPartidoPorId(id)

    suspend fun crearPartido(partido: PartidoEntity): Long =
        partidoDao.insertarPartido(partido)

    suspend fun actualizarPartido(partido: PartidoEntity) =
        partidoDao.actualizarPartido(partido)

    suspend fun eliminarPartido(partido: PartidoEntity) =
        partidoDao.eliminarPartido(partido)

    fun obtenerRotacion(partidoId: Long): Flow<RotacionActualEntity?> =
        rotacionDao.obtenerRotacionPorPartido(partidoId)

    suspend fun guardarRotacion(rotacion: RotacionActualEntity) =
        rotacionDao.insertarRotacion(rotacion)

    suspend fun actualizarRotacion(rotacion: RotacionActualEntity) =
        rotacionDao.actualizarRotacion(rotacion)
}
