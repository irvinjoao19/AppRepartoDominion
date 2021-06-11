package com.dsige.reparto.dominion.data.workManager

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.OperarioGps
import com.dsige.reparto.dominion.data.local.repository.AppRepository
import com.dsige.reparto.dominion.helper.Mensaje
import com.dsige.reparto.dominion.helper.Util
import com.google.android.gms.location.LocationServices
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class GpsWork @Inject
internal constructor(
    val context: Context, workerParams: WorkerParameters, private val roomRepository: AppRepository
) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val isGPSEnabled = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val checkLocationPermission: Boolean =
            context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (isGPSEnabled.isProviderEnabled(LocationManager.GPS_PROVIDER) && checkLocationPermission) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        getUsuario(location)
                    }
                }
        } else {
            notificationGps(context)
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

    private fun notificationGps(context: Context) {
        val manager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val nBuilder = getBasicNotificationBuilder(context)
            nBuilder.setContentTitle("Mensaje Gps")
                .setContentText("Gps desactivado, necesitas activarlo para poder continuar \n con el servicio")
                .setContentIntent(pendingIntent)
            val mMediaPlayer = MediaPlayer.create(context, R.raw.ic_error)
            mMediaPlayer.start()

            val nManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            nManager.notify(TIMER_ID, nBuilder.build())
        }
    }

    private fun getBasicNotificationBuilder(context: Context)
            : NotificationCompat.Builder {
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setDefaults(0)
            .setSound(notificationSound)
    }

    @TargetApi(26)
    private fun NotificationManager.createNotificationChannel(
        channelID: String,
        channelName: String,
        playSound: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val nChannel = NotificationChannel(channelID, channelName, channelImportance)
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            this.createNotificationChannel(nChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID_TIMER = "enable_gps"
        private const val CHANNEL_NAME_TIMER = "Dsige_Enable_Gps"
        private const val TIMER_ID = 0
    }
}