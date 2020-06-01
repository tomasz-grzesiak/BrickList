package com.example.bricklist.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.bricklist.fragments.ProjectNameFragment
import com.example.bricklist.R
import com.example.bricklist.logic.DBHandler
import kotlinx.android.synthetic.main.activity_starting.*


class StartingActivity : AppCompatActivity() {

    private val dbHandler: DBHandler =
        DBHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        setSupportActionBar(toolbar)

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
            R.id.menu_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK)
            refreshProjects()
    }

    fun addProject(v: View) {
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivityForResult(intent, 100)
    }

    private fun refreshProjects() {
        content.removeAllViews()

        val projects = dbHandler.getProjects()

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
}
