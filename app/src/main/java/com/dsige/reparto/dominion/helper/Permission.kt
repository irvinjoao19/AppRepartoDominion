package com.dsige.reparto.dominion.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dsige.reparto.dominion.data.workManager.BatteryWork
import com.dsige.reparto.dominion.data.workManager.GpsWork
import com.dsige.reparto.dominion.data.workManager.RepartoWork
import java.util.concurrent.TimeUnit

object Permission {

    val PERMISSION_ALL = 1
    val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )

    val CAMERA_REQUEST = 1
    val GALERY_REQUEST = 2
    val SPEECH_REQUEST = 3
    val REGISTRO_REQUEST = 4
    val UPDATE_REGISTRO_REQUEST = 5
    val CANCEL_REGISTOR_REQUEST = 6

    val POLICY_REQUEST = 7


    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun executeGpsWork(context: Context) {
//        val downloadConstraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
        val locationWorker =
            PeriodicWorkRequestBuilder<GpsWork>(15, TimeUnit.MINUTES)
//                .setConstraints(downloadConstraints)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "Gps-Work",
                ExistingPeriodicWorkPolicy.REPLACE,
                locationWorker
            )
        Util.toastMensaje(context, "Servicio Gps Activado")
    }

    fun closeGpsWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("Gps-Work")
    }

    fun executeBatteryWork(context: Context) {
        val locationWorker =
            PeriodicWorkRequestBuilder<BatteryWork>(15, TimeUnit.MINUTES)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "Battery-Work",
                ExistingPeriodicWorkPolicy.REPLACE,
                locationWorker
            )
    }

    fun closeBatteryWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("Battery-Work")
    }

    fun executeRepartoWork(context: Context) {
        val locationWorker =
            PeriodicWorkRequestBuilder<RepartoWork>(1, TimeUnit.HOURS)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "Reparto-Work",
                ExistingPeriodicWorkPolicy.REPLACE,
                locationWorker
            )
    }

    fun closeRepartoWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("Reparto-Work")
    }
}