package com.dsige.reparto.dominion.ui.workManager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.reparto.dominion.data.local.model.OperarioGps
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.data.local.model.Recibo
import com.dsige.reparto.dominion.data.local.repository.AppRepository
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
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
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Provider

class GpsWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        LocationServices.getFusedLocationProviderClient(context)
            ?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getUsuario(location)
                }
            }
        return Result.success()
    }

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return GpsWork(
                appContext,
                params,
                repository.get()
            )
        }
    }

    private fun getUsuario(l: Location) {
        roomRepository.getUsuarioId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
                override fun onNext(t: Int) {
                    saveGps(
                        OperarioGps(
                            t,
                            l.latitude.toString(),
                            l.longitude.toString(),
                            "",
                            Util.getFechaActual(),
                            1
                        )
                    )
                }
            })
    }

    private fun saveGps(e: OperarioGps) {
        roomRepository.insertGps(e)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    sendGps()
                }
            })
    }

    private fun sendGps() {
        roomRepository.getSendGps()
            .flatMap { observable ->
                Observable.fromIterable(observable).flatMap { a ->
                    Observable.zip(
                        Observable.just(a),
                        roomRepository.saveOperarioGps(a), { _, mensaje -> mensaje })
                }
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(t: Throwable) {
                    Log.i("TAG", t.toString())
                }

                override fun onNext(t: Mensaje) {
                    updateEnabledGps(t)
                }
            })
    }

    private fun updateEnabledGps(t: Mensaje) {
        roomRepository.updateEnabledGps(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }
}