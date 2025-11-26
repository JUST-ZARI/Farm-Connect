package com.example.farmconnect

import android.app.Application
import com.example.farmconnect.manager.CartManager
import com.google.firebase.FirebaseApp

class FarmConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase is auto-initialized with google-services.json
        // This class can be used for additional initialization if needed
        FirebaseApp.initializeApp(this)
        CartManager.init(this)
    }
}

