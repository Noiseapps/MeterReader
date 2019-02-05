package pl.noiseapps.meterstatus.readings.model

import org.joda.time.format.DateTimeFormat

val dateTimeFormat = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")

data class MeterReading(
    val value: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
) {

    constructor(value: Long, ts: String) : this(value, dateTimeFormat.parseMillis(ts))

    fun getDateString() = dateTimeFormat.print(timestamp)


    companion object {
        fun dummy(): List<MeterReading> {
            return listOf(
                MeterReading(55130, "04-12-2018 21:23"),
                MeterReading(63828, "05-12-2018 21:20"),
                MeterReading(73695, "07-12-2018 20:00"),
                MeterReading(85053, "10-12-2018 19:00"),
                MeterReading(91410, "12-12-2018 22:40"),
                MeterReading(97764, "13-12-2018 19:45"),
                MeterReading(101087, "14-12-2018 19:50"),
                MeterReading(109808, "17-12-2018 21:15"),
                MeterReading(113806, "18-12-2018 23:23"),
                MeterReading(117195, "19-12-2018 20:00"),
                MeterReading(123662, "20-12-2018 23:52"),
                MeterReading(126136, "21-12-2018 18:00"),
                MeterReading(141840, "26-12-2018 15:45"),
                MeterReading(149767, "29-12-2018 23:00"),
                MeterReading(155156, "31-12-2018 13:00"),
                MeterReading(160830, "02-01-2019 09:00"),
                MeterReading(165822, "03-01-2019 20:31")
            )
        }
    }
}