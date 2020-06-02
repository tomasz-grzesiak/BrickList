package com.example.bricklist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import com.example.bricklist.R
import com.example.bricklist.fragments.PartFragment
import com.example.bricklist.logic.DBHandler
import kotlinx.android.synthetic.main.activity_project.content
import kotlinx.android.synthetic.main.activity_project.toolbar
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        setSupportActionBar(toolbar)

        val parts = DBHandler(this).getProjectParts(intent.extras!!.getInt("projectID"))
        if (parts.size == 0) {
            val text = TextView(this)
            text.text = "W projekcie nie ma żadnych elementów"
            text.setPadding(50)
            text.textSize = 20.0f
            text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            content.addView(text)
            return
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        for (part in parts) {
            val fragment = PartFragment()
            val args = Bundle()
            args.putInt("id", part.id)
            args.putInt("inventoryID", part.inventoryID)
            args.putInt("typeID", part.typeID)
            args.putInt("itemID", part.itemID)
            args.putInt("quantityInSet", part.quantityInSet)
            args.putInt("quantityInStore", part.quantityInStore)
            args.putInt("colorID", part.colorID)
            fragment.arguments = args
            fragmentTransaction.add(R.id.content, fragment)
        }

        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_archive -> archive()
            R.id.menu_xml -> writeXML()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun archive(): Boolean {
        DBHandler(this).archive(intent.extras!!.getInt("projectID"))
        return true
    }

    private fun writeXML(): Boolean {

        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()

        val rootElement: Element = doc.createElement("INVENTORY")

        val dbHandler = DBHandler(this)
        val parts = dbHandler.getMissingProjectParts(intent.extras!!.getInt("projectID"))
        if (parts.size == 0) {
            Toast.makeText(this, "Posiadasz wszytskie elementy tego projektu! Nie utworzono pliku wynikowego", Toast.LENGTH_LONG).show()
            return true
        }
        for (part in parts) {
            val item = doc.createElement("ITEM")

            val itemType = doc.createElement("ITEMTYPE")
            itemType.appendChild(doc.createTextNode(dbHandler.getCodeFromID("ItemTypes", part.typeID)))
            item.appendChild(itemType)

            val itemID = doc.createElement("ITEMID")
            itemID.appendChild(doc.createTextNode(dbHandler.getCodeFromID("Parts", part.itemID)))
            item.appendChild(itemID)

            val color = doc.createElement("COLOR")
            color.appendChild(doc.createTextNode(dbHandler.getCodeFromID("Colors", part.colorID)))
            item.appendChild(color)

            val minimum = doc.createElement("MINQTY")
            minimum.appendChild((doc.createTextNode((part.quantityInSet - part.quantityInStore).toString())))
            item.appendChild(minimum)

            rootElement.appendChild(item)
        }

        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()

        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val outDir = File(this.filesDir, "output")
        if (!outDir.exists()) {
            outDir.mkdir()
        }

        val file = File(outDir, intent.extras!!.getString("projectName") + ".xml")

        transformer.transform(DOMSource(doc), StreamResult(file))
        return true
    }
}
