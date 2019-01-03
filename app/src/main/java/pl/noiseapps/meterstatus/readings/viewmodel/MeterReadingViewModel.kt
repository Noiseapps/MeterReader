package pl.noiseapps.meterstatus.readings.viewmodel

import io.reactivex.Observable
import pl.noiseapps.meterstatus.readings.model.MeterReading

class MeterReadingViewModel {

    lateinit var meterReadingObservable : Observable<MeterReading>

    fun getReadings() : Observable<MeterReading> {
        meterReadingObservable = Observable.fromIterable(MeterReading.dummy())
        return meterReadingObservable
    }

}