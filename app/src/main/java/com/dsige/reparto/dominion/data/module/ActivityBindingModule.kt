package com.dsige.reparto.dominion.data.module

import com.dsige.reparto.dominion.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindRepartoActivity(): RepartoActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Form::class])
    internal abstract fun bindFormRepartoActivity(): FormRepartoActivity

    @ContributesAndroidInjector
    internal abstract fun bindPendingLocationMapsActivity(): PendingLocationMapsActivity

    @ContributesAndroidInjector
    internal abstract fun bindFirmActivity(): FirmActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewCameraActivity(): PreviewCameraActivity
}