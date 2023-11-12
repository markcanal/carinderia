package com.example.carinderia.core

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarinderiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeAppCheck(this)
    }

    private fun initializeAppCheck(context: Context) {
        Firebase.initialize(context = context)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }
}