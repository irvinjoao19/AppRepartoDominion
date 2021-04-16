package com.dsige.reparto.dominion.data.module

import com.dsige.reparto.dominion.data.local.repository.*
import com.dsige.reparto.dominion.data.local.AppDataBase
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module(includes = [ViewModelModule::class])
class RetrofitModule {

    @Provides
    internal fun providesRetrofit(
        gsonFactory: GsonConverterFactory,
        rxJava: RxJava2CallAdapterFactory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(rxJava)
            .addConverterFactory(gsonFactory)
            .client(client)
            .build()
    }

    @Provides
    internal fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    internal fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().setLenient().create())
    }

    @Provides
    internal fun providesRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    internal fun provideService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    internal fun provideRepository(apiService: ApiService, database: AppDataBase): AppRepository {
        return AppRepoImp(apiService, database)
    }

    @Provides
    internal fun provideError(retrofit: Retrofit): ApiError {
        return ApiError(retrofit)
    }

    companion object {
        private val BASE_URL = "http://dominion-peru.com/webApiReparto/api/Migration/"
//        private val BASE_URL = "http://173.248.174.31/webApiReparto/api/Migration/"
    }
}