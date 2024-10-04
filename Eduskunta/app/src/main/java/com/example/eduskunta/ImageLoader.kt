package com.example.eduskunta

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.net.URL

val SERVER_PATHNAME = "https://avoindata.eduskunta.fi/"

// 04.10.2024 by Arman Yerkeshev 2214297
// This class is responsible for loading images from the server, employing caching mechanism
object ImageLoader {
    fun getImage(urlString: String?): ImageBitmap? {
        if (urlString == null) {
            return null
        }

        val url = URL(SERVER_PATHNAME + urlString)
        var res: Bitmap? = null
        runBlocking {
            val deferred = async(Dispatchers.IO) {
                res = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
            deferred.await()
        }
        return res?.asImageBitmap()
    }
}