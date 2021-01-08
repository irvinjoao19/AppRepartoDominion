package com.dsige.reparto.dominion.ui.workManager

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.data.local.model.Registro
import com.dsige.reparto.dominion.data.local.model.Recibo
import com.dsige.reparto.dominion.data.local.repository.AppRepository
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
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

class RepartoWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        sendReparto()
        return Result.success()
    }

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return RepartoWork(
                appContext,
                params,
                repository.get()
            )
        }
    }

    private fun sendReparto() {
        val ots: Observable<List<Registro>> =
            roomRepository.getRegistroTask()
        ots.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->

                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()

                val recibo: Recibo? = a.recibo
                if (recibo != null) {
                    if (recibo.firmaCliente.isNotEmpty()) {
                        val file = File(Util.getFolder(context), recibo.firmaCliente)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }
                val photos: List<Photo>? = a.photos
                if (photos != null) {
                    for (p: Photo in photos) {
                        if (p.rutaFoto.isNotEmpty()) {
                            val file = File(Util.getFolder(context), p.rutaFoto)
                            if (file.exists()) {
                                filePaths.add(file.toString())
                            }
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart(
                        "fotos",
                        file.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    )
                }

                val json = Gson().toJson(a)
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)
                val requestBody = b.build()
                Observable.zip(
                    Observable.just(a),
                    roomRepository.sendRegistroRx(requestBody),
                    { _, mensaje ->
                        mensaje
                    })
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(t: Throwable) {}
                override fun onNext(t: Mensaje) {
                    updateEnabledReparto(t)
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
}