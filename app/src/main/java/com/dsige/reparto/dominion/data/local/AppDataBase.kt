package com.dsige.reparto.dominion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dsige.reparto.dominion.data.local.dao.*
import com.dsige.reparto.dominion.data.local.model.*

@Database(
    entities = [
        Usuario::class,
        Formato::class,
        Reparto::class,
        Servicio::class,
        Parametro::class,
        Registro::class,
        Photo::class,
        Recibo::class
    ],
    version = 2, // version 1 en play store
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun formatoDao(): FormatoDao
    abstract fun parametroDao(): ParametroDao
    abstract fun servicioDao(): ServicioDao
    abstract fun repartoDao(): RepartoDao
    abstract fun registroDao(): RegistroDao
    abstract fun photoDao(): PhotoDao
    abstract fun reciboDao(): ReciboDao

    companion object {
        @Volatile
        var INSTANCE: AppDataBase? = null
        val DB_NAME = "reparto_db"
    }

    fun getDatabase(context: Context): AppDataBase {
        if (INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "reparto-db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return INSTANCE!!
    }
}