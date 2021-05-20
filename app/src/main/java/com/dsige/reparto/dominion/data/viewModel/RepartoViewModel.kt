package com.dsige.reparto.dominion.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.data.local.repository.*
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RepartoViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()
    val mensajeRecibo = MutableLiveData<String>()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getRepartos(): LiveData<List<Reparto>> {
        return roomRepository.getRepartos()
    }

    fun getRepartoById(id: Int): LiveData<Reparto> {
        return roomRepository.getRepartoById(id)
    }

    fun deletePhoto(p: Photo, code: String, context: Context) {
        roomRepository.deletePhoto(p, code, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    fun getCodigoBarra(barCode: String, i: Int): Observable<Reparto> {
        return roomRepository.getCodigoBarra(barCode, i)
    }

    fun getAllRegistro(i: Int): LiveData<Int> {
        return roomRepository.getAllRegistro(i)
    }

    fun saveReparto(r: Registro) {
        roomRepository.saveReparto(r)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    fun getPhotoReparto(id: Int, i: Int): LiveData<List<Photo>> {
        return roomRepository.getPhotoReparto(id, i)
    }

    fun getRegistroId(repartoId: Int): Observable<Int> {
        return roomRepository.getRegistroId(repartoId)
    }

    fun getFormato(tipo: Int): LiveData<List<Formato>> {
        return roomRepository.getFormato(tipo)
    }

    fun validateRegistroRecibo(r: Recibo, validation: Int) {
        if (r.piso == 0) {
            mensajeError.value = "Ingrese Nro Piso"
            return
        }
        if (r.formatoVivienda == 0) {
            mensajeError.value = "Ingrese Vivienda"
            return
        }
        if (r.formatoVivienda == 11) {
            if (r.otrosVivienda.isEmpty()) {
                mensajeError.value = "Ingrese Otros Vivienda"
                return
            }
        }
        if (r.formatoCargoColor == 0) {
            mensajeError.value = "Ingrese Color/Fachada"
            return
        }
        if (r.formatoCargoColor == 16) {
            if (r.otrosCargoColor.isEmpty()) {
                mensajeError.value = "Ingrese Otros Color Fachada"
                return
            }
        }
        if (r.formatoCargoPuerta == 0) {
            mensajeError.value = "Ingrese Puerta"
            return
        }
        if (r.formatoCargoPuerta == 20) {
            if (r.otrosCargoPuerta.isEmpty()) {
                mensajeError.value = "Ingrese Otros Puerta"
                return
            }
        }
        if (r.formatoCargoColorPuerta == 0) {
            mensajeError.value = "Ingrese Color Puerta"
            return
        }
        if (r.formatoCargoColorPuerta == 25) {
            if (r.otrosCargoColorPuerta.isEmpty()) {
                mensajeError.value = "Ingrese Otros Color Puerta"
                return
            }
        }
        if (r.formatoCargoRecibo == 0) {
            mensajeError.value = "Ingrese Recibido por."
            return
        }
        insertOrUpdateRegistroRecibo(r, validation)
    }

    private fun insertOrUpdateRegistroRecibo(r: Recibo, validation: Int) {
        roomRepository.insertOrUpdateRegistroRecibo(r)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    if (validation == 2) {
                        mensajeSuccess.value = "Favor de firmar para completar formulario"
                    } else {
                        mensajeSuccess.value = "Recibo Guardado"
                    }
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getReciboByFk(repartoId: Int): LiveData<Recibo> {
        return roomRepository.getReciboByFk(repartoId)
    }

    fun updateRepartoEnvio(repartoId: Int) {
        roomRepository.updateRepartoEnvio(repartoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeRecibo.value = "Ok"
                }
            })
    }

    fun getRecibos(repartoId: Int): LiveData<List<Recibo>> {
        return roomRepository.getRecibos(repartoId)
    }

    fun getUpdateRegistro(repartoId: Int, name: String) {
        roomRepository.getUpdateRegistro(repartoId, name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Datos Generales Guardados"
                }
            })
    }

    fun savePhotoReparto(p: Photo) {
        roomRepository.savePhotoReparto(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }
            })
    }

    fun getRegistros(): LiveData<List<Registro>> {
        return roomRepository.getRegistros()
    }

    fun getPhotos(): LiveData<List<Photo>> {
        return roomRepository.getPhotos()
    }

    fun sendFiles(context: Context) {
        val files = roomRepository.getFiles()
        files.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val b = MultipartBody.Builder()
                if (a.isNotEmpty()) {
                    val file = File(Util.getFolder(context), a)
                    if (file.exists()) {
                        b.addFormDataPart(
                            "fotos", file.name,
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"), file
                            )
                        )
                    }
                }
                b.setType(MultipartBody.FORM)
                val body = b.build()
                Observable.zip(
                    Observable.just(a), roomRepository.sendPhotos(body), { _, mensaje ->
                        mensaje
                    })
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(m: String) {
//                    Log.i("TAG", m)
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                            e1.printStackTrace()
//                            Log.i("TAG", e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.message)
                    }
                }

                override fun onComplete() {
                    sendDataTask()
                }
            })
    }

    private fun sendDataTask() {
        val ots: Observable<List<Registro>> =
            roomRepository.getRegistroTask()
        ots.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val json = Gson().toJson(a)
//                Log.i("TAG", json)
                val body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
                Observable.zip(
                    Observable.just(a),
                    roomRepository.sendRegistro(body), { _, m -> m })
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(t: Throwable) {
                    mensajeError.value = t.message
                }

                override fun onNext(t: Mensaje) {
                    updateEnabledReparto(t)
                }

                override fun onComplete() {
                    mensajeSuccess.value = "Datos Enviados"
                }
            })
    }

    private fun updateEnabledReparto(t: Mensaje) {
        roomRepository.closeRegistro(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    fun generarArchivo(
        nameImg: String,
        context: Context,
        direccion: String,
        latitud: String,
        longitud: String,
        id: Int
    ) {
        Util.getPhotoAdjunto(
            nameImg, context, direccion,
            latitud, longitud, id
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Photo> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: Photo) {
                    savePhotoReparto(t)
                }
            })
    }
}