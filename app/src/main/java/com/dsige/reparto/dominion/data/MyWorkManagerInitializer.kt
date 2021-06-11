package com.dsige.reparto.dominion.data

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import androidx.work.Configuration
import androidx.work.WorkManager
import com.dsige.reparto.dominion.data.workManager.WorkManagerFactory
import javax.inject.Inject

class MyWorkManagerInitializer  : DummyContentProvider() {

    @Inject
    lateinit var daggerAwareWorkerFactory: WorkManagerFactory

    private fun configureWorkManager() {
        val config = Configuration.Builder()
            .setWorkerFactory(daggerAwareWorkerFactory)
            .build()
        WorkManager.initialize(context!!, config)
    }

    override fun onCreate(): Boolean {
        configureWorkManager()
        return true
    }
}
abstract class DummyContentProvider : ContentProvider() {
    override fun onCreate() = true

    override fun insert(uri: Uri, values: ContentValues?): Nothing? = null
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Nothing? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?) = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0

    override fun getType(uri: Uri): Nothing? = null
}