package com.example.bricklist.activities

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.bricklist.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        val filename = "settings"
        val file = InputStreamReader(openFileInput(filename))
        val br = BufferedReader(file)
        urlPrefixText.setText(br.readLine())
        if (br.readLine() == "true") {
            archiveText.text = "Wyświetlaj archiwalne projekty"
            archiveSwitch.isChecked = true
        } else {
            archiveText.text = "Nie wyświetlaj archiwalnych projektów"
            archiveSwitch.isChecked = false
        }
        file.close()
    }


    override fun finish() {
        val filename = "settings"
        val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
        file.write(urlPrefixText.text.toString() + "\n")
        if (archiveSwitch.isChecked) {
            file.write("true\n")
        } else {
            file.write("false\n")
        }
        file.flush()
        file.close()
        setResult(Activity.RESULT_OK, null)
        super.finish()
    }

    fun change(v: View) {
        if (archiveSwitch.isChecked) {
            archiveText.text = "Wyświetlaj archiwalne projekty"
        } else {
            archiveText.text = "Nie wyświetlaj archiwalnych projektów"
        }
    }
}
