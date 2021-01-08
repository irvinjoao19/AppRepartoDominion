package com.dsige.reparto.dominion.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.reparto.dominion.data.local.model.*
import com.dsige.reparto.dominion.data.local.repository.*
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
                override fun onComplete() {
                    if (validation == 2) {
                        mensajeSuccess.value = "Favor de firmar para completar formulario"
                    } else {
                        mensajeSuccess.value = "Recibo Guardado"
                    }
                }

                override fun onSubscribe(d: Disposable) {

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
}