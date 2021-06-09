package com.inFlow.moneyManager

import android.app.Application
import com.inFlow.moneyManager.di.dataModule
import com.inFlow.moneyManager.di.viewModelModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
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