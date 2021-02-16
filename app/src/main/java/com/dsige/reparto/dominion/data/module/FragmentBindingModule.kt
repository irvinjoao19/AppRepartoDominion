package com.dsige.reparto.dominion.data.module

import com.dsige.reparto.dominion.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {

    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providMainFragment(): MainFragment

        @ContributesAndroidInjector
        internal abstract fun providSendFragment(): SendFragment
    }

    @Module
    abstract class Form {
        @ContributesAndroidInjector
        internal abstract fun providGeneralFragment(): GeneralFragment

        @ContributesAndroidInjector
        internal abstract fun providFirmFragment(): FirmFragment
    }

}