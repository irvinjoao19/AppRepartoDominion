package com.dsige.reparto.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody

interface AppRepository {

    //usuario
    fun getUsuario(): LiveData<Usuario>
    fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String, token: String
    ): Observable<Usuario>

    fun getUsuarioId(): Observable<Int>

    fun insertUsuario(u: Usuario): Completable
    fun deleteSesion(): Completable
    fun deleteSync(): Completable
    fun getSync(u: Int, v: String): Observable<Sync>
    fun saveSync(s: Sync): Completable

    //reparto
    fun getRepartos(): LiveData<List<Reparto>>
    fun getRepartoById(id: Int): LiveData<Reparto>
    fun deletePhoto(p: Photo, code: String, context: Context): Completable
    fun getCodigoBarra(barCode: String, i: Int): Observable<Reparto>
    fun getAllRegistro(i: Int): LiveData<Int>
    fun saveReparto(r: Registro): Completable
    fun getPhotoReparto(id: Int, i: Int): LiveData<List<Photo>>
    fun getRegistroId(repartoId: Int): Observable<Int>

    fun getRegistroTask(): Observable<List<Registro>>
    fun sendRegistroRx(body: RequestBody): Observable<Mensaje>
    fun closeRegistro(t: Mensaje): Completable

    fun getFormato(tipo: Int): LiveData<List<Formato>>
    fun insertOrUpdateRegistroRecibo(r: Recibo): Completable
    fun getReciboByFk(id: Int): LiveData<Recibo>
    fun updateRepartoEnvio(repartoId: Int): Completable

    fun getUpdateRegistro(repartoId: Int, name: String): Completable
    fun getRecibos(repartoId: Int): LiveData<List<Recibo>>
    fun savePhotoReparto(p: Photo): Completable



    //gps
    fun insertGps(e: OperarioGps): Completable
    fun getSendGps(): Observable<List<OperarioGps>>
    fun saveOperarioGps(e: OperarioGps): Observable<Mensaje>
    fun updateEnabledGps(t: Mensaje): Completable
}