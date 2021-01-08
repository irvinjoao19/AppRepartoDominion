package com.dsige.reparto.dominion.data.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.reparto.dominion.data.local.model.*
import com.google.gson.Gson
import com.dsige.reparto.dominion.data.local.repository.ApiError
import com.dsige.reparto.dominion.data.local.repository.AppRepository
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsuarioViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()

    val user: LiveData<Usuario>
        get() = roomRepository.getUsuario()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getLogin(usuario: String, pass: String, imei: String, version: String, token: String) {
        roomRepository.getUsuarioService(usuario, pass, imei, version, token)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Usuario> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(usuario: Usuario) {
                    insertUsuario(usuario, version)
                }

                override fun onError(t: Throwable) {
                    if (t is HttpException) {
                        val body = t.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    } else {
                        mensajeError.postValue(t.message)
                    }
                }

                override fun onComplete() {
                }
            })
    }

    fun insertUsuario(u: Usuario, v: String) {
        roomRepository.insertUsuario(u)
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    sync(u.iD_Operario, v)
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun logout(login: String) {
        deleteUser(login)
//        var mensaje = ""
//        roomRepository.getLogout(login)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : Observer<Mensaje> {
//                override fun onSubscribe(d: Disposable) {
//
//                }
//
//                override fun onNext(m: Mensaje) {
//                    mensaje = m.mensaje
//                }
//
//                override fun onError(t: Throwable) {
//                    if (t is HttpException) {
//                        val body = t.response().errorBody()
//                        try {
//                            val error = retrofit.errorConverter.convert(body!!)
//                            mensajeError.postValue(error.Message)
//                        } catch (e1: IOException) {
//                            e1.printStackTrace()
//                        }
//                    } else {
//                        mensajeError.postValue(t.message)
//                    }
//                }
//
//                override fun onComplete() {
//                    deleteUser(mensaje)
//                }
//            })
    }


    private fun deleteUser(mensaje: String) {
        roomRepository.deleteSesion()
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Close"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun sync(u: Int, v: String) {
        roomRepository.deleteSync()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    roomRepository.getSync(u, v)
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<Sync> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onComplete() {}
                            override fun onNext(t: Sync) {
                                insertSync(t)
                            }

                            override fun onError(e: Throwable) {
                                if (e is HttpException) {
                                    val body = e.response().errorBody()
                                    try {
                                        val error = retrofit.errorConverter.convert(body!!)
                                        mensajeError.postValue(error!!.Message)
                                    } catch (e1: IOException) {
                                        e1.printStackTrace()
                                        Log.i("TAG", e1.toString())
                                    }
                                } else {
                                    mensajeError.postValue(e.toString())
                                }
                            }
                        })
                }
            })
    }

    private fun insertSync(p: Sync) {
        roomRepository.saveSync(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Sincronización Completa"
                }
            })
    }


    fun generarArchivo(nameImg: String, context: Context, data: Intent) {
        Util.getFolderAdjunto(nameImg, context, data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = nameImg
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", e.toString())
                }
            })
    }
}