package com.example.pulseplay

import android.app.Application
import android.content.Context

// PulsePlayApplication.kt
class PulsePlayApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}