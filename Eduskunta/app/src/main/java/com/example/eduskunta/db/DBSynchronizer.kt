package com.example.eduskunta.db

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.eduskunta.NetworkAPI
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

// 28.09.2024 by Arman Yerkeshev 2214297
// This is class is responsible for periodically synchronizing the local database with the server
object DBSynchronizer {
    fun start(context: Context) {
        val uploadRequest = PeriodicWorkRequestBuilder<DBWorker>(15, TimeUnit.MINUTES).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(uploadRequest)
    }
}

// 29.09.2024 by Arman Yerkeshev 2214297
// This class fetches data from the server and saves it to local database
class DBWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "eduskunta-db"
        ).build()
        val dao = db.memberDao()

        try {
            thread {
                var parliamentMembers: List<ParliamentMember>? = NetworkAPI.apiService.loadMainData()?.execute()?.body()
                if (parliamentMembers == null) {
                    throw Exception("Failed to fetch main data")
                }

                val extraData = NetworkAPI.apiService.loadExtraData()?.execute()?.body()
                if (extraData == null) {
                    throw Exception("Failed to fetch extra data")
                }

                // Add extra data to parliament members' list
                parliamentMembers = parliamentMembers.map { memberData1: ParliamentMember ->
                    val memberData2 = extraData.find { it.hetekaId == memberData1.hetekaId }
                    if (memberData2 != null) {
                        return@map memberData1.copy(
                            twitter = memberData2.twitter,
                            bornYear = memberData2.bornYear,
                            constituency = memberData2.constituency
                        )
                    } else {
                        return@map memberData1
                    }
                }

                dao.insertAll(parliamentMembers)
                Thread.sleep(1000)
                Log.d("DBG", dao.getAll().toString())
            }

            if(db.isOpen) {
                db.openHelper.close()
            }
            return Result.success()
        } catch (e: Exception) {
            if(db.isOpen) {
                db.openHelper.close()
            }
            return Result.failure()
        }
    }
}