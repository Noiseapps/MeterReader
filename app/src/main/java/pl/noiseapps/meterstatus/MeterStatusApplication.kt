package pl.noiseapps.meterstatus

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

open class MeterStatusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}