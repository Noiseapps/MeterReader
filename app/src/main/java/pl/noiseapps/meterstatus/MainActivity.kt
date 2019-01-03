package pl.noiseapps.meterstatus

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.io.FileUtils
import pl.noiseapps.meterstatus.readings.adapters.ReadingsAdapter
import pl.noiseapps.meterstatus.readings.model.MeterReading
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    lateinit var adapter: ReadingsAdapter
    val file: File by lazy { File(filesDir, "readings.json") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { this.addNewEntry() }

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
        adapter = ReadingsAdapter(readings.asSequence().sortedBy { -it.timestamp }.toMutableList())
        inputsList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        inputsList.adapter = adapter
    }

    private fun addNewEntry() {
        val editText = EditText(this)
        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
        editText.setHint(R.string.current_reading)
        editText.setText("123123")

        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.new_value)
            .setMessage(R.string.new_value_msg)
            .setView(editText)
        builder.setPositiveButton(R.string.add) { dialog, which ->
            adapter.addItem(MeterReading(editText.text.toString().toLong()))
            inputsList.scrollToPosition(0)
            FileUtils.write(file, Gson().toJson(adapter.items), Charset.defaultCharset())
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
