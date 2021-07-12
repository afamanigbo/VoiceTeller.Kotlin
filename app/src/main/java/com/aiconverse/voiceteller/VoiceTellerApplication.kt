package com.aiconverse.voiceteller

import android.app.Application
import timber.log.Timber

class VoiceTellerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}