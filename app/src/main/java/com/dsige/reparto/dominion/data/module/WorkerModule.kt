package com.dsige.reparto.dominion.data.module

import com.dsige.reparto.dominion.ui.workManager.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RepartoWork::class)
    internal abstract fun bindRepartoWork(repartoWork: RepartoWork.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(GpsWork::class)
    internal abstract fun bindGpsWork(repartoWork: GpsWork.Factory): ChildWorkerFactory
}