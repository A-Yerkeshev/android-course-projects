package com.example.eduskunta

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
class PMViewModel(private val hetekaid: Int?): ViewModel() {
    private val dao = PMDatabase.getInstance().memberDao()
    private val members: Flow<List<ParliamentMember>> = dao.getAll()
    val member: Flow<ParliamentMember> = members.transform {
        if (hetekaid == null) {
            emit(it.first())
        } else {
            emit(it.first { it.hetekaId == hetekaid })
        }
    }
    val nextMember: Flow<ParliamentMember> = members.transform {
        val idx = (it.indexOfFirst { it.hetekaId == member.first().hetekaId } + 1) % it.size
        emit(it[idx])
    }
    val previousMember: Flow<ParliamentMember> = members.transform {
        val idx = (it.indexOfFirst { it.hetekaId == member.first().hetekaId } - 1 + it.size) % it.size
        emit(it[idx])
    }
    var notes by mutableStateOf("")

    fun updateMember(member: ParliamentMember?) {
        viewModelScope.launch {
            if (member != null) {
                dao.update(member)
            }
        }
    }
}