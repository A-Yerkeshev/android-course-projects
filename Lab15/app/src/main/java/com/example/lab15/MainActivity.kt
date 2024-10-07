package com.example.lab15

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab15.ui.theme.Lab15Theme
import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageDir = getExternalFilesDir( Environment.DIRECTORY_MUSIC)!!

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No audio recorder access")
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1);
        }

        enableEdgeToEdge()
        setContent {
            Lab15Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UI(
                        this,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private val filename: String = "audiofile.raw"
    lateinit var storageDir: File
        private set
    lateinit var recFile: File
        private set
    var recRunning: Boolean = false

    @SuppressLint("MissingPermission")
    fun recordAudio() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                recFile = File(storageDir.toString() + "/" + filename)
                val outputStream = FileOutputStream(recFile)
                val bufferedOutputStream = BufferedOutputStream(outputStream)
                val dataOutputStream = DataOutputStream(bufferedOutputStream)
                val minBufferSize = AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT)
                val aFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
                val recorder = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(aFormat)
                    .setBufferSizeInBytes(minBufferSize)
                    .build()
                val audioData = ByteArray(minBufferSize)
                recorder.startRecording()
                recRunning = true
                while (recRunning) {
                    val numofBytes = recorder.read(audioData, 0, minBufferSize)
                    if(numofBytes>0) {
                        dataOutputStream.write(audioData)
                    }
                }
                recorder.stop()
                dataOutputStream.close()
                Log.d("DBG", "Stopped recording")
            } catch (e: Exception) {
                Log.d("DBG", "Error recording audio file")
            }
        }
    }

    fun playAudio() {
        GlobalScope.launch(Dispatchers.IO) {
            recRunning = false

            val istream: InputStream = FileInputStream(File(storageDir.toString() + "/" + filename))

            val minBufferSize = AudioTrack.getMinBufferSize(
                44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val aBuilder = AudioTrack.Builder()
            val aAttr: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val aFormat: AudioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .build()
            val track = aBuilder.setAudioAttributes(aAttr)
                .setAudioFormat(aFormat)
                .setBufferSizeInBytes(minBufferSize)
                .build()
            track.setVolume(0.2f)
            track.play()
            var i = 0
            val buffer = ByteArray(minBufferSize)
            try {
                i = istream.read(buffer, 0, minBufferSize)
                while (i != -1) {
                    track.write(buffer, 0, i)
                    i = istream.read(buffer, 0, minBufferSize)
                }
            } catch (e: IOException) {
                Log.e("DBG", "Stream read error $e")
            }
            try {
                istream.close()
            } catch (e: IOException) {
                Log.e("DBG", "Close error $e")
            }
            track.stop()
            track.release()
        }
    }
}

@Composable
fun UI(activity: MainActivity, modifier: Modifier = Modifier) {
    val text = remember {
        mutableStateOf("Start recording")
    }

    Column(modifier = Modifier.padding(12.dp, 60.dp, 12.dp, 12.dp)) {
        Button(onClick = {
            if (text.value == "Start recording") {
                activity.recordAudio()
                text.value = "Stop recording"
            } else {
                activity.recRunning = false
                text.value = "Start recording"
            }
        }) {
            Text(text = text.value)
        }
        Button(onClick = {
            activity.playAudio()
        }) {
            Text(text = "Play recording")
        }
    }
}