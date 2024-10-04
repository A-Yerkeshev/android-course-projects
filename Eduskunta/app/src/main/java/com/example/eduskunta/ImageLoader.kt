package com.example.eduskunta

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.FileOutputStream
import java.net.URL

val SERVER_PATHNAME = "https://avoindata.eduskunta.fi/"

// 04.10.2024 by Arman Yerkeshev 2214297
// This class is responsible for loading images from the server, employing caching mechanism
object ImageLoader {
    fun getImage(urlString: String?): ImageBitmap? {
        if (urlString == null) {
            return null
        }

        // Check if image was already loaded
        // - if it was - decode file
        // - if it wasn't - download it and save to cache
        val filename: String = urlString.substringAfterLast("/")

        try {
            return BitmapFactory.decodeFile(filename).asImageBitmap()
        } catch (e: Exception) {
            val url = URL(SERVER_PATHNAME + urlString)
            val res: Bitmap? = runBlocking {
                val deferred = async(Dispatchers.IO) {
                    try {
                        return@async BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    } catch (e: Exception) {
                        return@async null
                    }
                }
                return@runBlocking deferred.await()
            }
            cacheImage(filename, res)
            return res?.asImageBitmap()
        }
    }

    fun cacheImage(filename: String, bitmap: Bitmap?) {
        if (bitmap == null) {
            return
        }

        val context = PMApplication.appContext
        val fileOutputStream: FileOutputStream =
            context.openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }
}