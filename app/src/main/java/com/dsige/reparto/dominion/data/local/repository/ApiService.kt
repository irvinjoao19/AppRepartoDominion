package com.dsige.reparto.dominion.data.local.repository

import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
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
    @POST("SaveNew")
    fun sendRegistroRx(@Body query: RequestBody): Observable<Mensaje>

    @POST("SaveEstadoMovil")
    fun saveEstadoMovil(@Body movil: RequestBody): Call<Mensaje>

    @POST("SaveGpsOperario")
    fun saveOperarioGps(@Body gps: RequestBody): Observable<Mensaje>
}