package com.dsige.reparto.dominion.data.module

import android.app.Application
import androidx.room.Room
import com.dsige.reparto.dominion.data.local.dao.*
import com.dsige.reparto.dominion.data.local.AppDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(application: Application): AppDataBase {
        if (AppDataBase.INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (AppDataBase.INSTANCE == null) {
                    AppDataBase.INSTANCE = Room.databaseBuilder(
                        application.applicationContext,
                        AppDataBase::class.java, AppDataBase.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return AppDataBase.INSTANCE!!
    }

    @Provides
    internal fun provideUsuarioDao(appDataBase: AppDataBase): UsuarioDao {
        return appDataBase.usuarioDao()
    }

    @Provides
    internal fun provideFormatoDao(appDataBase: AppDataBase): FormatoDao {
        return appDataBase.formatoDao()
    }

    @Provides
    internal fun provideParametroDao(appDataBase: AppDataBase): ParametroDao {
        return appDataBase.parametroDao()
    }

    @Provides
    internal fun provideServicioDao(appDataBase: AppDataBase): ServicioDao {
        return appDataBase.servicioDao()
    }

    @Provides
    internal fun provideRepartoDao(appDataBase: AppDataBase): RepartoDao {
        return appDataBase.repartoDao()
    }

    @Provides
    internal fun provideRegistroDao(appDataBase: AppDataBase): RegistroDao {
        return appDataBase.registroDao()
    }

    @Provides
    internal fun providePhotoDao(appDataBase: AppDataBase): PhotoDao {
        return appDataBase.photoDao()
    }

    @Provides
    internal fun provideReciboDao(appDataBase: AppDataBase): ReciboDao {
        return appDataBase.reciboDao()
    }

    @Provides
    internal fun provideGpsDao(appDataBase: AppDataBase): OperarioGpsDao {
        return appDataBase.operarioGpsDao()
    }

    @Provides
    internal fun provideBatteryDao(appDataBase: AppDataBase): OperarioBatteryDao {
        return appDataBase.operarioBatteryDao()
    }
}