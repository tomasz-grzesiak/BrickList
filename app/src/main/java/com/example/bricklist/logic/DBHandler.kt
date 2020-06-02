package com.example.bricklist.logic

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.core.database.getBlobOrNull
import java.util.*
import kotlin.collections.ArrayList

class DBHandler(context: Context) :SQLiteOpenHelper(context,
    "BrickList.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getProjects(archived: Boolean): ArrayList<Project> {
        val db = this.readableDatabase
        val cursor: Cursor
        cursor = if (archived) {
            db.rawQuery("SELECT * FROM Inventories", null)
        } else {
            db.rawQuery("SELECT * FROM Inventories WHERE Active = 1", null)
        }
        val inventories = ArrayList<Project>()
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                inventories.add(
                    Project(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2) == 1,
                        cursor.getInt(3)
                    )
                )
            }
        }
        db.close()
        return inventories
    }

    fun getProjectParts(projectID: Int): ArrayList<Part>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM InventoriesParts WHERE InventoryID = $projectID", null)
        val parts = ArrayList<Part>()
        while (cursor.moveToNext()) {
            parts.add(Part(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7))
            )
        }
        cursor.close()
        db.close()
        return parts
    }

    fun getMissingProjectParts(projectID: Int): ArrayList<Part> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM InventoriesParts WHERE InventoryID = $projectID AND QuantityInSet - QuantityInStore > 0", null)
        val parts = ArrayList<Part>()
        while (cursor.moveToNext()) {
            parts.add(Part(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7))
            )
        }
        cursor.close()
        db.close()
        return parts
    }

    private fun getID(db: SQLiteDatabase, table: String, code: String): Int {
        val cursor = db.rawQuery("SELECT id FROM $table WHERE Code = \"$code\"", null)
        val id =  if (cursor.moveToFirst()) cursor.getInt(0) else -1
        cursor.close()
        return id
    }

    fun getCodeFromID(table: String, id: Int): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT Code FROM $table WHERE id = $id", null)
        val code = if (cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        db.close()
        return code
    }

    fun getPartDescription(itemID: Int, colorID: Int): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT Parts.Name, Colors.Name, Parts.Code FROM Parts, Colors WHERE Parts.id = $itemID AND Colors.id = $colorID", null)

        var description = ""
        if (cursor.moveToFirst()) {
            description = "${cursor.getString(0)}\n${cursor.getString(1)} [${cursor.getString(2)}]"
        }
        cursor.close()
        db.close()
        return description
    }

    fun getPartImage(itemID: Int, colorID: Int): Bitmap? {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT Codes.Code, Codes.Image FROM Codes WHERE ItemID = $itemID AND ColorId = $colorID", null)

        var image: Bitmap? = null
        if (cursor.moveToFirst()) {
            image = if (cursor.getBlobOrNull(1) == null) {
                val code = cursor.getString(0)
                val imageByteArray = PictureHandler().execute(code).get()
                if (imageByteArray != null) {
                    val values = ContentValues()
                    values.put("Image", imageByteArray)
                    db.update("Codes", values, "Code = $code", null)
                    BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                }
                null
            } else {
                val imageByteArray = cursor.getBlob(1)
                BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            }
        }
        val imageByteArray = PictureHandler().execute(null, getCodeFromID("Parts", itemID), getCodeFromID("Colors", colorID)).get()
        if (imageByteArray != null)
            image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        cursor.close()
        db.close()
        return image
    }

    fun addProject(name: String, parts: ArrayList<XMLItem>): Boolean {
        val values = ContentValues()
        values.put("Name", name)
        values.put("LastAccessed", Date().time)
        val db = this.writableDatabase
        val id = db.insert("Inventories", null, values).toInt()
        for (part in parts) {
            addInventoryPart(db, part, id)
        }
        db.close()
        if (id != -1)
            return true
        return false
    }

    private fun addInventoryPart(db: SQLiteDatabase, part: XMLItem, invID: Int): Boolean {
        val values = ContentValues()
        values.put("InventoryID", invID)
        values.put("TypeID", getID(db, "ItemTypes", part.itemType))
        values.put("ItemID", getID(db, "Parts", part.itemID))
        values.put("QuantityInSet", part.quantity)
        values.put("ColorID", getID(db, "Colors", part.color.toString()))
        val id = db.insert("InventoriesParts", null, values).toInt()
        if (id != -1)
            return true
        return false
    }

    fun setQuantity(id: Int, quantity: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("QuantityInStore", quantity)
        val count = db.update("InventoriesParts", values, "id = $id", null)
        db.close()
        if (count == 1)
            return true
        return false
    }

    fun archive(projectID: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("Active", 0)
        db.update("Inventories", values, "id = $projectID", null)
        db.close()
    }

    fun fillInventory() {
        val values = ContentValues()
        values.put("id", 1)
        values.put("Name", "Pierwszy projekt")
        values.put("LastAccessed", 20)
        val db = this.writableDatabase
        Log.i("INSERT", db.insert("Inventories", null, values).toString())
        values.put("id", 2)
        values.put("Name", "Drugi projekt")
        values.put("LastAccessed", 20)
        Log.i("INSERT", db.insert("Inventories", null, values).toString())
        values.put("id", 3)
        values.put("Name", "Trzeci projekt")
        values.put("LastAccessed", 20)
        Log.i("INSERT", db.insert("Inventories", null, values).toString())
        db.close()
    }
}