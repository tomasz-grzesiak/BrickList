package com.example.bricklist.logic

class XMLItem() {
    var itemType: String = ""
    var itemID: String = ""
    var quantity: Int = 0
    var color: Int = 0
    var extra: String = ""
    var alternate: String = ""

    override fun toString(): String {
        return "{itemType: $itemType, itemID: $itemID, quantity: $quantity, color: $color, extra: $extra, alternate: $alternate}\n"
    }
}