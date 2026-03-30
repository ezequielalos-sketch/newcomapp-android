package com.pilar.newcomapp.di

import android.content.Context
import androidx.room.Room
import com.pilar.newcomapp.data.local.NewcomDatabase
import com.pilar.newcomapp.data.local.dao.PartidoDao
import com.pilar.newcomapp.data.local.dao.RotacionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewcomDatabase {
        return Room.databaseBuilder(
            context,
            NewcomDatabase::class.java,
            "newcom_database"
        ).build()
    }

    @Provides
    fun providePartidoDao(db: NewcomDatabase): PartidoDao = db.partidoDao()

    @Provides
    fun provideRotacionDao(db: NewcomDatabase): RotacionDao = db.rotacionDao()
}
