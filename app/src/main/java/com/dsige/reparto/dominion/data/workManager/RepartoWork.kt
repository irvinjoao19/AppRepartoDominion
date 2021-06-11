package com.dsige.reparto.dominion.data.workManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.reparto.dominion.data.local.model.Registro
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
import javax.inject.Inject
import javax.inject.Provider

class RepartoWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        sendFiles(context)
        return Result.success()
    }

    class Factory @Inject constructor(private val repository: Provider<AppRepository>) :
        ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return RepartoWork(appContext, params, repository.get())
        }
    }

    private fun sendFiles(context: Context) {
        val files = roomRepository.getFiles(context)
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
                override fun onNext(m: String) {}
                override fun onError(e: Throwable) {}
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