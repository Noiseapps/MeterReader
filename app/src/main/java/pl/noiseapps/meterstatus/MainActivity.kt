package pl.noiseapps.meterstatus

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.io.FileUtils
import pl.noiseapps.meterstatus.readings.adapters.ReadingsAdapter
import pl.noiseapps.meterstatus.readings.model.MeterReading
import pl.noiseapps.meterstatus.readings.model.dateTimeFormat
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    lateinit var adapter: ReadingsAdapter
    val file: File by lazy { File(filesDir, "readings.json") }

    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { this.addNewEntry() }

        val database = FirebaseDatabase.getInstance()
        ref = database.reference

//        Gson().fromJson<List<MeterReading>>(
//            FileReader(file),
//            object : TypeToken<List<MeterReading>>() {}.type
//        ).toMutableList().sortedBy { it.timestamp }.forEach {
////            println(it)
//            ref.push().setValue(it)
//        }

        inputsList.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, true)
        adapter = ReadingsAdapter(mutableListOf())
        inputsList.adapter = adapter
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
//                Log.d("EVENT LISTENER", error.message)
            }

            override fun onDataChange(data: DataSnapshot) {
                val children = data.children.toList().mapNotNull { it.getValue(MeterReading::class.java) }
                Log.d("EVENT LISTENER", children.size.toString())

                adapter.setItems(children.reversed())
//                FileUtils.write(file, Gson().toJson(children.sortedBy { -it.timestamp }), Charset.defaultCharset())
                setupChart(children)
            }
        })
    }

    private fun setupList(readings: MutableList<MeterReading>) {
        adapter = ReadingsAdapter(readings.asSequence().sortedBy { -it.timestamp }.toMutableList())
        inputsList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        inputsList.adapter = adapter
    }

    private fun setupChart(readings: List<MeterReading>) {
        val entryList = readings.map { Entry(it.timestamp.toFloat(), it.value.toFloat(), it) }
        val dataSet = LineDataSet(entryList, "Odczyty")
        with(chart.xAxis) {
            axisMinimum = readings.minBy { it.timestamp }!!.timestamp.toFloat()
            axisMaximum = readings.maxBy { it.timestamp }!!.timestamp.toFloat()
            valueFormatter = IAxisValueFormatter { value, _ -> dateTimeFormat.print(value.toLong()) }
            labelCount = 2
        }

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                title = ""
            }

            override fun onValueSelected(e: Entry, h: Highlight) {
                title = (e.data as MeterReading).getDateString()
            }

        })

        dataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun getReadings(): MutableList<MeterReading> {
        if (!file.exists()) file.createNewFile()
        val readings = try {
            Gson().fromJson<List<MeterReading>>(
                FileReader(file),
                object : TypeToken<List<MeterReading>>() {}.type
            ).toMutableList()
        } catch (ex: Exception) {
            Log.d("Activity", "Jebło", ex)
            mutableListOf<MeterReading>()
        }
        if (readings.isEmpty()) {
            readings.addAll(MeterReading.dummy())
        }
        return readings
    }

    private fun addNewEntry() {
        val editText = EditText(this)
        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
        editText.setHint(R.string.current_reading)

        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.new_value)
            .setMessage(R.string.new_value_msg)
            .setView(editText)
        builder.setPositiveButton(R.string.add) { dialog, which ->
            val meterReading = MeterReading(editText.text.toString().toLong())
            ref.push().setValue(meterReading)
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog.setOnShowListener {
            editText.requestFocus()
        }
        dialog.show()
    }

    private fun addReadingToChart(meterReading: MeterReading) {
        chart.data.addEntry(meterReading.let { Entry(it.timestamp.toFloat(), it.value.toFloat(), it) }, 0)
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }
}
