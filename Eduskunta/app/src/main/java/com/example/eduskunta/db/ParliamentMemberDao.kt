package com.example.eduskunta.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 29.09.2024 by Arman Yerkeshev 2214297
// Data Access Object for ParliamentMember class
@Dao
interface ParliamentMemberDao {
    @Query("SELECT * FROM parliamentmember")
    fun getAll(): Flow<List<ParliamentMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(members: List<ParliamentMember>)

    @Delete
    fun delete(member: ParliamentMember)
}