package com.example.eduskunta

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.eduskunta.db.DBSynchronizer

// 30.09.2024 by Arman Yerkeshev 2214297
// Helper class to easily access application context. Launches DBSynchronizer on application start
class PMApplication : Application(){
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        DBSynchronizer.start()
    }
}