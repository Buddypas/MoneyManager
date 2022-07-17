package com.inFlow.moneyManager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
//        startKoin {
//            androidContext(this@BaseApp)
//            androidLogger(Level.ERROR)
//            modules(
//                listOf(
//                    dataModule,
//                    viewModelModule
//                )
//            )
//        }

        // Initialize timber
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}