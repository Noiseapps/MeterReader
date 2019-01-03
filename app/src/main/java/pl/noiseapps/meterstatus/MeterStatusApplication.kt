package pl.noiseapps.meterstatus

import android.app.Application
import com.google.firebase.FirebaseApp

open class MeterStatusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}