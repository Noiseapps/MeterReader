package pl.noiseapps.meterstatus.readings.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.format.DateTimeFormat
import pl.noiseapps.meterstatus.R
import pl.noiseapps.meterstatus.readings.model.MeterReading

class ReadingsAdapter(val items: MutableList<MeterReading>) : RecyclerView.Adapter<ReadingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    fun addItem(meterReading: MeterReading) {
        items.add(0, meterReading)
        notifyItemInserted(0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        lateinit var meterReading: MeterReading
        val dateTextView: TextView = view.findViewById(R.id.date)
        val valueTextView: TextView = view.findViewById(R.id.value)

        fun setItem(meterReading: MeterReading) {
            this.meterReading = meterReading
            dateTextView.text = DateTimeFormat.forPattern("HH:mm dd:MM:yyyy").print(meterReading.timestamp)
            valueTextView.text = "%03.3f".format(meterReading.value / 1000.0)
        }
    }
}