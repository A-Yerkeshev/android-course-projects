package com.example.lab08

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab08.ui.theme.Lab08Theme
import kotlinx.coroutines.*
import java.net.URL
import kotlin.coroutines.*

val url = URL("https://static.wikia.nocookie.net/monstermovies/images/5/5a/Crab_People.png/revision/latest?cb=20141007012051")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(32.dp)) {
                        Text("Lab08")
                        ImageFrame(downloadImage(url))
                    }
                }
            }
        }
    }
}

fun downloadImage(url: URL): Bitmap? {
    var res: Bitmap? = null
    runBlocking {
        val deferred = async(Dispatchers.IO) {
            res = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
        deferred.await()
    }
    return res
}

@Composable
fun ImageFrame(content: Bitmap?, modifier: Modifier = Modifier) {
    return if (content != null) {
        Image(content.asImageBitmap(), "Image")
    } else {
        Text("Loading image...")
    }
}

//@Preview(showBackground = true)
@Composable
fun ImageFramePreview() {
    Lab08Theme {
        ImageFrame(downloadImage(url))
    }
}