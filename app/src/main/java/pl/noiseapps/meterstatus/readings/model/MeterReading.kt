package pl.noiseapps.meterstatus.readings.model

import java.util.*

data class MeterReading(
    val value: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun dummy() : List<MeterReading>{
            val r = Random()
            return listOf(
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong()),
                MeterReading(r.nextLong())
            )
        }
    }
}