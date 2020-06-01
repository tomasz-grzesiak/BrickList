package com.example.bricklist.logic

import android.os.AsyncTask
import java.io.InputStream
import java.net.URL

class PictureHandler : AsyncTask<String, Int, ByteArray>() {

    private val newUrl = "https://www.lego.com/service/bricks/5/2/"
    private val oldUrl = "http://img.bricklink.com/P/"

    override fun doInBackground(vararg params: String?): ByteArray? {
        var iStream: InputStream? = null
        var result: ByteArray? = null
        try {
            val url = URL(this.newUrl + params[0])
//            } else {
//                URL(this.oldUrl)
//            }
            url.openConnection().connect()
            iStream = url.openStream()
            result = iStream.readBytes()
        } catch (e: Exception) {
        } finally {
            iStream?.close()
        }
        return result
    }

}