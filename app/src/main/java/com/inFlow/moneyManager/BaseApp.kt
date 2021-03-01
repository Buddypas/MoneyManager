package com.inFlow.moneyManager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApp: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize timber
        Timber.plant(Timber.DebugTree())
    }
}