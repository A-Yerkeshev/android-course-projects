package com.example.bluetoothle

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : Activity() {
    val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)

    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), 1)
            }

            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                // Add the device to a list or display its name and address
                Log.d("DBG", "Device found: ${device.name} - ${device.address}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), 1)
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
        }
        registerReceiver(receiver, discoverDevicesIntent)
        bluetoothAdapter.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}


//@Composable
//fun UI(mBluetoothAdapter: BluetoothAdapter, model: MainViewModel = MainViewModel(), hasPermissions: Boolean = false) {
//    val context = LocalContext.current
//    val devices: List<ScanResult>? by model.scanResults.observeAsState(null)
//    val fScanning: Boolean by model.fScanning.observeAsState(false)
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//        Button(
//            onClick = {
//                startBleScan()
//            },
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8c46db)),
//            shape = RectangleShape,
//            modifier = Modifier
//                .padding(4.dp, 48.dp, 4.dp, 24.dp)
//        ) {
//            Text(
//                text = "Start Scanning",
//                fontSize = 28.sp,
//            )
//        }
//        devices?.forEach { device ->
//            Text(
//                text = device.toString(),
//                color = Color.Gray,
//                fontSize = 24.sp,
//                modifier = Modifier
//                    .padding(4.dp)
//            )
//        }
//    }
//}