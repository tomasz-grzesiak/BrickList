package com.example.bricklist.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import com.example.bricklist.R
import com.example.bricklist.logic.DBHandler
import kotlinx.android.synthetic.main.fragment_part.*

class PartFragment : Fragment() {

    private var id: Int? = null
    private var inventoryID: Int? = null
    private var typeID: Int? = null
    private var itemID: Int? = null
    private var quantityInSet: Int? = null
    private var quantityInStore: Int? = null
    private var colorID: Int? = null
    private var extra: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.id = arguments?.getInt("id")
        this.inventoryID = arguments?.getInt("inventoryID")
        this.typeID = arguments?.getInt("typeID")
        this.itemID = arguments?.getInt("itemID")
        this.quantityInSet = arguments?.getInt("quantityInSet")
        this.quantityInStore = arguments?.getInt("quantityInStore")
        this.colorID = arguments?.getInt("colorID")
        this.extra = arguments?.getInt("extra")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_part, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dbHandler = DBHandler(activity?.baseContext!!)
        description.text = dbHandler.getPartDescription(this.itemID!!, this.colorID!!)
        val image = dbHandler.getPartImage(this.itemID!!, this.colorID!!)
        if (image != null) {
            imageView.setImageBitmap(image)
        }
        val text = "$quantityInStore / $quantityInSet"
        quantity.text = text
        if (quantityInStore == quantityInSet){
            quantity.setTextColor(Color.GREEN)
        }
        reduceButton.setOnClickListener {
            if (this.quantityInStore!! > 0) {
                if (DBHandler(activity?.baseContext!!).setQuantity(this.id!!, this.quantityInStore!!-1)) {
                    this.quantityInStore = this.quantityInStore!! - 1
                    val newText = "$quantityInStore / $quantityInSet"
                    quantity.text = newText
                    if (this.quantityInStore!! < this.quantityInSet!!){
                        quantity.setTextColor(Color.GRAY)
                    }
                } else {
                    Toast.makeText(activity?.baseContext!!, "Wystąpił błąd. Załaduj ponownie dane", Toast.LENGTH_LONG).show()
                }
            }
        }
        addButton.setOnClickListener {
            if (this.quantityInStore!! < this.quantityInSet!!) {
                if (DBHandler(activity?.baseContext!!).setQuantity(this.id!!, this.quantityInStore!!+1)) {
                    this.quantityInStore = this.quantityInStore!! + 1
                    val newText = "$quantityInStore / $quantityInSet"
                    quantity.text = newText
                    if (this.quantityInStore == this.quantityInSet){
                        quantity.setTextColor(Color.GREEN)
                    }
                } else {
                    Toast.makeText(activity?.baseContext!!, "Wystąpił błąd. Załaduj ponownie dane", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
