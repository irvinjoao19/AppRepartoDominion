package com.dsige.reparto.dominion.data.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.data.local.AppDataBase
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String, token: String
    ): Observable<Usuario> {
        return apiService.getLogin(usuario, password, imei, version, token)
    }

    override fun getUsuarioId(): Observable<Int> {
        return Observable.create {
            val id = dataBase.usuarioDao().getUsuarioIdTask()
            it.onNext(id)
            it.onComplete()
        }
    }

    override fun insertUsuario(u: Usuario): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().insertUsuarioTask(u)
        }
    }

    override fun deleteSesion(): Completable {
        return Completable.fromAction {
            dataBase.formatoDao().deleteAll()
            dataBase.photoDao().deleteAll()
            dataBase.reciboDao().deleteAll()
            dataBase.registroDao().deleteAll()
            dataBase.repartoDao().deleteAll()
            dataBase.servicioDao().deleteAll()
            dataBase.usuarioDao().deleteAll()
        }
    }

    override fun deleteSync(): Completable {
        return Completable.fromAction {
            dataBase.formatoDao().deleteAll()
            dataBase.photoDao().deleteAll()
            dataBase.reciboDao().deleteAll()
            dataBase.registroDao().deleteAll()
            dataBase.repartoDao().deleteAll()
            dataBase.servicioDao().deleteAll()
            dataBase.repartoDao().deleteAll()
        }
    }

    override fun getSync(u: Int, v: String): Observable<Sync> {
        return apiService.getSync(u, v)
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {
            val c1: List<Servicio>? = s.servicios
            if (c1 != null) {
                dataBase.servicioDao().insertServicioListTask(c1)
            }
            val c2: List<Parametro>? = s.parametros
            if (c2 != null) {
                dataBase.parametroDao().insertParametroListTask(c2)
            }
            val c3: List<Reparto>? = s.repartoLectura
            if (c3 != null) {
                dataBase.repartoDao().insertRepartoListTask(c3)
            }
            val c4: List<Formato>? = s.formatos
            if (c4 != null) {
                dataBase.formatoDao().insertFormatoListTask(c4)
            }
        }
    }

    override fun getRepartos(): LiveData<List<Reparto>> {
        return dataBase.repartoDao().getRepartos()
    }

    override fun getRepartoById(id: Int): LiveData<Reparto> {
        return dataBase.repartoDao().getRepartoById(id)
    }

    override fun deletePhoto(p: Photo, code: String, context: Context): Completable {
        return Completable.fromAction {
            Util.deletePhoto(p.rutaFoto, context)
            dataBase.photoDao().deletePhotoTask(p)
            dataBase.repartoDao().enableReparto(code)
        }
    }

    override fun getCodigoBarra(barCode: String, i: Int): Observable<Reparto> {
        return Observable.create {
            val r: Reparto? = dataBase.repartoDao().getCodigoBarra(barCode, i)
            if (r == null) {
                it.onError(Throwable("Codigo no encontrado"))
                it.onComplete()
                return@create
            }
            it.onNext(r)
            it.onComplete()
        }
    }

    override fun getAllRegistro(i: Int): LiveData<Int> {
        return dataBase.registroDao().getAllRegistro(i)
    }

    override fun saveReparto(r: Registro): Completable {
        return Completable.fromAction {
            val data: Registro? = dataBase.registroDao().getValidateRegistroIdTask(r.iD_Suministro)
            if (data == null) {
                dataBase.registroDao().insertRegistroTask(r)
            }

            if (r.estado == 1) {
                dataBase.repartoDao().disableReparto(r.iD_Suministro)
            }
        }
    }

    override fun getPhotoReparto(id: Int, i: Int): LiveData<List<Photo>> {
        return dataBase.photoDao().getPhotoReparto(id, i)
    }

    override fun getRegistroId(repartoId: Int): Observable<Int> {
        return Observable.create {
            val id = dataBase.registroDao().getRegistroId(repartoId)
            it.onNext(id)
            it.onComplete()
        }
    }

    override fun getPhotos(): LiveData<List<Photo>> {
        return dataBase.photoDao().getPhotos()
    }

    override fun getRegistroTask(): Observable<List<Registro>> {
        return Observable.create {
            val list = ArrayList<Registro>()
            val r = dataBase.registroDao().getRegistroTask(1)
            if (r.isEmpty()) {
                it.onError(Throwable("No hay datos para enviar"))
                it.onComplete()
                return@create
            }

            for (p: Registro in r) {
                p.recibo = dataBase.reciboDao().getReciboTaskByFk(p.id)
                p.photos = dataBase.photoDao().getPhotoByFk(p.iD_Suministro)
                list.add(p)
            }

            it.onNext(r)
            it.onComplete()
        }
    }

    override fun getRegistros(): LiveData<List<Registro>> {
        return dataBase.registroDao().getRegistros()
    }

    override fun closeRegistro(t: Mensaje): Completable {
        return Completable.fromAction {
            val d: Registro? = dataBase.registroDao().getRegistroByIdTask(t.codigoBase)
            if (d != null) {
                dataBase.photoDao().closePhoto(d.iD_Suministro)
            }
            dataBase.registroDao().closeRegistro(t.codigoBase)
        }
    }

    override fun getFormato(tipo: Int): LiveData<List<Formato>> {
        return dataBase.formatoDao().getFormato(tipo)
    }

    override fun insertOrUpdateRegistroRecibo(r: Recibo): Completable {
        return Completable.fromAction {
            val re: Recibo? = dataBase.reciboDao().getReciboById(r.repartoId)
            if (re == null) {
                dataBase.reciboDao().insertReciboTask(r)
            } else {
                dataBase.reciboDao().updateReciboTask(r)
            }
        }
    }

    override fun getReciboByFk(id: Int): LiveData<Recibo> {
        return dataBase.reciboDao().getReciboByFk(id)
    }

    override fun updateRepartoEnvio(repartoId: Int): Completable {
        return Completable.fromAction {
            dataBase.registroDao().activeRegistro(repartoId)
            val r: Registro? = dataBase.registroDao().getRegistroIdTask(repartoId)
            if (r != null) {
                dataBase.repartoDao().updateReparto(r.iD_Suministro)
            }
        }
    }

    override fun getUpdateRegistro(repartoId: Int, name: String): Completable {
        return Completable.fromAction {
            dataBase.reciboDao().updateFirma(repartoId, name)
        }
    }

    override fun getRecibos(repartoId: Int): LiveData<List<Recibo>> {
        return dataBase.reciboDao().getRecibos(repartoId)
    }

    override fun savePhotoReparto(p: Photo): Completable {
        return Completable.fromAction {
            val f: Photo? = dataBase.photoDao().getPhotoByName(p.rutaFoto)
            if (f == null) {
                dataBase.photoDao().insertPhotoTask(p)
            }
        }
    }

    override fun insertGps(e: OperarioGps): Completable {
        return Completable.fromAction {
            dataBase.operarioGpsDao().insertOperarioGpsTask(e)
        }
    }

    override fun getSendGps(): Observable<List<OperarioGps>> {
        return Observable.create {
            val gps: List<OperarioGps> = dataBase.operarioGpsDao().getOperarioGpsTask()
            it.onNext(gps)
            it.onComplete()
        }
    }

    override fun saveOperarioGps(e: OperarioGps): Observable<Mensaje> {
        val json = Gson().toJson(e)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.saveOperarioGps(body)
    }

    override fun updateEnabledGps(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.operarioGpsDao().updateEnabledGps(t.codigoBase)
        }
    }

    override fun insertBattery(e: OperarioBattery): Completable {
        return Completable.fromAction {
            dataBase.operarioBatteryDao().insertOperarioBatteryTask(e)
        }
    }

    override fun getSendBattery(): Observable<List<OperarioBattery>> {
        return Observable.create {
            val gps: List<OperarioBattery> = dataBase.operarioBatteryDao().getOperarioBatteryTask()
            it.onNext(gps)
            it.onComplete()
        }
    }

    override fun saveOperarioBattery(e: OperarioBattery): Observable<Mensaje> {
        val json = Gson().toJson(e)
//        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.saveOperarioBattery(body)
    }

    override fun updateEnabledBattery(t: Mensaje): Completable {
        return Completable.fromAction {
            dataBase.operarioBatteryDao().updateEnabledBattery(t.codigo)
        }
    }


    override fun getFiles(context: Context): Observable<List<String>> {
        return Observable.create {
            val files: ArrayList<String> = ArrayList()
            val v: List<Registro> = dataBase.registroDao().getRegistroTask(1)
            if (v.isNotEmpty()) {
                for (r: Registro in v) {
                    val recibo: Recibo? = dataBase.reciboDao().getReciboTaskByFk(r.id)
                    if (recibo != null) {
                        if (recibo.firmaCliente.isNotEmpty()) {
                            val file = File(Util.getFolder(context), recibo.firmaCliente)
                            if (file.exists()) {
                                files.add(recibo.firmaCliente)
                            }
                        }
                    }
                }
            }

            val photos = dataBase.photoDao().getPhotosTask()
            for (p: Photo in photos) {
                val file = File(Util.getFolder(context), p.rutaFoto)
                if (file.exists()) {
                    files.add(p.rutaFoto)
                }
            }

            it.onNext(files)
            it.onComplete()
        }
    }

    override fun sendPhotos(body: RequestBody): Observable<String> {
        return apiService.sendPhoto(body)
    }

    override fun sendRegistro(body: RequestBody): Observable<Mensaje> {
        return apiService.sendRegistro(body)
    }

//    override fun insertReparto(): Completable {
//        return Completable.fromAction {
//            val a = Reparto()
//            a.id_Reparto = 1
//            a.id_Operario_Reparto = 1
//            a.foto_Reparto = 2
//            a.id_observacion = 1
//            a.Suministro_Medidor_reparto = "100"
//            a.Suministro_Numero_reparto = "100"
//            a.Direccion_Reparto = "Mz D lote 20 Urb Virgen de Fatima"
//            a.Cod_Orden_Reparto = "10"
//            a.Cod_Actividad_Reparto = "10"
//            a.Cliente_Reparto = "Irvin Joao"
//            a.CodigoBarra = "1"
//            a.estado = 1
//            a.activo = 1
//            a.latitud = ""
//            a.longitud = ""
//            a.isActive = true
//
//            dataBase.repartoDao().insertRepartoTask(a)
//        }
//    }
}