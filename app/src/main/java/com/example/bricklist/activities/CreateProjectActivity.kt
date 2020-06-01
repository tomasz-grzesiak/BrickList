package com.example.bricklist.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist.R
import com.example.bricklist.logic.DBHandler
import com.example.bricklist.logic.XMLHandler
import kotlinx.android.synthetic.main.activity_create_project.*


class CreateProjectActivity : AppCompatActivity() {

    private val url: String = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)
        setSupportActionBar(toolbar)
    }

    override fun finish() {
        setResult(Activity.RESULT_OK,null)
        super.finish()
    }

    fun addProject(v: View) {
        if (projectCode.text.toString() == "") {
            Toast.makeText(this, "Należy podać kod zestawu", Toast.LENGTH_LONG).show()
            return
        }
        val suffix = projectCode.text.toString() + ".xml"
        val xmlHelper = XMLHandler("${this.url}$suffix", "$filesDir/$suffix")
        xmlHelper.execute()
        Toast.makeText(this, "Pobieranie danych...", Toast.LENGTH_LONG).show() //TODO info że trwa tworzenie porjektu
        val fileDownloaded = xmlHelper.get()

        if (fileDownloaded != "OK") {
            Toast.makeText(this, fileDownloaded, Toast.LENGTH_LONG).show()
            return
        }

        val parts = xmlHelper.parseSourceXML()

        val dbHandler =  DBHandler(this)
        if (dbHandler.addProject(projectName.text.toString(), parts)) {
            Toast.makeText(this, "Utworzono pomyślnie nowy projekt", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Nie udało się utworzyć nowego projektu", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}
