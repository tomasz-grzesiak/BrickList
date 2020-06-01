package com.example.bricklist.logic

import android.graphics.Bitmap

class Part {
    val id: Int
    val inventoryID: Int
    val typeID: Int
    val itemID: Int
    val quantityInSet: Int
    val quantityInStore: Int
    val colorID: Int
    val extra: Int

    constructor(id: Int, inventoryID: Int, typeID: Int, itemID: Int, quantityInSet: Int,
                QuantityInStore: Int, colorId: Int, extra: Int) {
        this.id = id
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.quantityInStore = QuantityInStore
        this.colorID = colorId
        this.extra = extra
    }
}