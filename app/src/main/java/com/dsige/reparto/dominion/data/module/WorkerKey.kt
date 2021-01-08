package com.dsige.reparto.dominion.data.module

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass
import kotlin.annotation.MustBeDocumented

//@MustBeDocumented
//@MapKey
//@kotlin.annotation.Target(
//    AnnotationTarget.FUNCTION,
//    AnnotationTarget.PROPERTY_GETTER,
//    AnnotationTarget.PROPERTY_SETTER
//)
//@kotlin.annotation.Retention

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(
    val value: KClass<out ListenableWorker>
)