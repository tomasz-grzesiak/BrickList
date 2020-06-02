package com.example.bricklist.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.bricklist.fragments.ProjectNameFragment
import com.example.bricklist.R
import com.example.bricklist.logic.DBHandler
import kotlinx.android.synthetic.main.activity_starting.*
import java.io.*


class StartingActivity : AppCompatActivity() {

    private val dbHandler: DBHandler =
        DBHandler(this)
    private var showArchived: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        setSupportActionBar(toolbar)

        if (!File("$dataDir/databases/BrickList.db").exists()) {
            try {
                val iStream = assets.open("BrickList.db")
                val databaseDir = File(dataDir, "databases")
                if (!databaseDir.exists())
                    databaseDir.mkdir()

                val oFileStream = FileOutputStream(File("$databaseDir/BrickList.db"))
                iStream.copyTo(oFileStream)
                iStream.close()
                oFileStream.flush()
                oFileStream.close()
            } catch (e: Exception) {
                throw Exception()
            }
        }

        readSettings()
        refreshProjects()
    }

    override fun onResume() {
        super.onResume()
        readSettings()
        refreshProjects()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_settings -> settings()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 300) {
                Log.i("RETURN", "RETURN")
                readSettings()
            }
            refreshProjects()
        }
    }

    fun addProject(v: View) {
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivityForResult(intent, 100)
    }

    private fun readSettings() {
        val filename = "settings"
        if (baseContext.getFileStreamPath(filename).exists()) {
            val file = InputStreamReader(openFileInput(filename))
            val br = BufferedReader(file)
            br.readLine()
            showArchived = br.readLine() == "true"
            file.close()
        } else {
            val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
            file.write("http://fcds.cs.put.poznan.pl/MyWeb/BL/\n")
            file.write(showArchived.toString() + "\n")
            file.flush()
            file.close()
        }
    }

    private fun refreshProjects() {
        content.removeAllViews()

        val projects = dbHandler.getProjects(showArchived)

        if (projects.size == 0) {
            val text = TextView(this)
            text.text = "Nie utworzono jeszcze żadnych projektów"
            text.setPadding(50)
            text.textSize = 20.0f
            text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            content.addView(text)
            return
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        for (project in projects) {
            val fragment = ProjectNameFragment()
            val args = Bundle()
            args.putInt("id", project.getId())
            args.putString("name", project.getName())
            fragment.arguments = args
            fragmentTransaction.add(R.id.content, fragment)
        }

        fragmentTransaction.commit()
    }

    private fun settings(): Boolean {
        startActivityForResult(Intent(this, SettingsActivity::class.java), 300)
        return true
    }
}
