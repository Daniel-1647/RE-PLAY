package com.daniel.replay
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReplayApplication: Application(){
    override fun onCreate() {
        super.onCreate()
    }
}