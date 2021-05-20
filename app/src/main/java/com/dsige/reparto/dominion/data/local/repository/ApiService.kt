package com.dsige.reparto.dominion.data.local.repository

import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @Headers("Cache-Control: no-cache")
    @GET("GetLogin")
    fun getLogin(
        @Query("user") user: String,
        @Query("password") password: String,
        @Query("imei") imei: String,
        @Query("version") version: String,
        @Query("token") token: String
    ): Observable<Usuario>

    @Headers("Cache-Control: no-cache")
    @GET("MigracionAll")
    fun getSync(
        @Query("operarioId") operarioId: Int,
        @Query("version") version: String
    ): Observable<Sync>

    @Headers("Cache-Control: no-cache")
    @POST("SaveRegistro")
    fun sendRegistro(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SavePhoto")
    fun sendPhoto(@Body query: RequestBody): Observable<String>

    @POST("SaveEstadoMovil")
    fun saveOperarioBattery(@Body movil: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveGpsOperario")
    fun saveOperarioGps(@Body gps: RequestBody): Observable<Mensaje>
}