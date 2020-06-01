package com.example.bricklist.logic

import android.os.AsyncTask
import java.io.*
import java.lang.Exception
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class XMLHandler(private val url: String, private val filename: String) : AsyncTask<String, Int, String>() {

    override fun doInBackground(vararg params: String?): String {
        var result = "OK"
        var fileStream: FileOutputStream? = null
        var iStream: InputStream? = null
        try {
            val newUrl = URL(this.url)
            val connection = newUrl.openConnection()
            connection.connect()
            iStream = newUrl.openStream()
            fileStream = FileOutputStream(filename)
            iStream.copyTo(fileStream)
        } catch (e: IOException) {
            result = "Błąd pobierania. Sprawdź połączenie sieciowe"
        } catch (e: FileNotFoundException) {
            result = "Błąd zapisu"
        } catch (e: Exception) {
            result = "Nieznany błąd"
        } finally {
            fileStream?.flush()
            fileStream?.close()
            iStream?.close()
        }
        return result
    }

    fun parseSourceXML(): ArrayList<XMLItem> {
        val xmlFile = File(filename)
        val xmlInput = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlInput.documentElement.normalize()
        val parts = xmlInput.getElementsByTagName("ITEM")

        val partsList = ArrayList<XMLItem>()
        for (i in 0 until parts.length) {
            val partItem = XMLItem()
            val attributes = parts.item(i).childNodes
            for (j in 0 until attributes.length) {
                val attribute = attributes.item(j)
                when(attribute.nodeName) {
                    "ITEMTYPE" -> partItem.itemType = attribute.textContent
                    "ITEMID" -> partItem.itemID = attribute.textContent
                    "QTY" -> partItem.quantity = attribute.textContent.toInt()
                    "COLOR" -> partItem.color = attribute.textContent.toInt()
                    "EXTRA" -> partItem.extra = attribute.textContent
                    "ALTERNATE" -> partItem.alternate = attribute.textContent
                }
            }
            if (partItem.alternate == "N") {
                partsList.add(partItem)
            }
        }
        return partsList
    }
}