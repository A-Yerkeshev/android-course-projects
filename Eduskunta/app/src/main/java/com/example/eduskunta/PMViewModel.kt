package com.example.eduskunta

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduskunta.db.PMDatabase
import com.example.eduskunta.db.ParliamentMember
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

// 30.09.2024 by Arman Yerkeshev 2214297
// Class for loading parliament members from database into application for later use by the UI
//class PMViewModel(private val hetekaid: Int?): ViewModel() {
class PMViewModel(): ViewModel() {
    private val dao = PMDatabase.getInstance().memberDao()
    val members: Flow<List<ParliamentMember>> = dao.getAll()
    lateinit var member: Flow<ParliamentMember>
        private set
    lateinit var nextMember: Flow<ParliamentMember>
        private set
    lateinit var previousMember: Flow<ParliamentMember>
        private set

    fun updateMember(updatedMember: ParliamentMember?) {
        viewModelScope.launch {
            if (updatedMember != null) {
                dao.update(updatedMember)
            }
        }
    }

    fun setMember(hetekaId: Int?) {
        member = members.transform {
            if (hetekaId == null) {
                emit(it.first())
            } else {
                emit(it.first { it.hetekaId == hetekaId })
            }
        }

        nextMember = members.transform {
            val idx = (it.indexOfFirst { it.hetekaId == member.first().hetekaId } + 1) % it.size
            emit(it[idx])
        }

        previousMember = members.transform {
            val idx = (it.indexOfFirst { it.hetekaId == member.first().hetekaId } - 1 + it.size) % it.size
            emit(it[idx])
        }
    }
}