package com.example.bluetoothle

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.bluetoothle.ui.theme.BluetoothLETheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mResults = java.util.HashMap<String, ScanResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothLETheme {
                UI(mBluetoothAdapter!!, hasPermissions = hasPermissions())
            }
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
    }

    private fun hasPermissions(): Boolean {
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            Log.d("DBG", "No Bluetooth LE capability")
            return false
        } else if (
            checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
            ) {
            Log.d("DBG", "No fine location access")
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH_SCAN), 1);
            return true // assuming that the user grants permission
        }
        return true
    }
}

@Composable
fun UI(mBluetoothAdapter: BluetoothAdapter, model: MainViewModel = MainViewModel(), hasPermissions: Boolean = false) {
    val context = LocalContext.current
    val devices: List<ScanResult>? by model.scanResults.observeAsState(null)
    val fScanning: Boolean by model.fScanning.observeAsState(false)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Button(
            onClick = {
                if (hasPermissions) {
                    model.scanDevices(mBluetoothAdapter.bluetoothLeScanner)
                    Log.d("DBG", devices.toString())
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8c46db)),
            shape = RectangleShape,
            modifier = Modifier
                .padding(4.dp, 48.dp, 4.dp, 24.dp)
        ) {
            Text(
                text = "Start Scanning",
                fontSize = 28.sp,
            )
        }
        devices?.forEach { device ->
            Text(
                text = device.toString(),
                color = Color.Gray,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}