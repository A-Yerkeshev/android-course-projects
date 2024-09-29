package com.example.eduskunta.db

import androidx.room.Database
import androidx.room.RoomDatabase

// 29.09.2024 by Arman Yerkeshev 2214297
// Database class, representing the local database
@Database(entities = [ParliamentMember::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun memberDao(): ParliamentMemberDao
}