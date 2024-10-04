package com.example.eduskunta

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.eduskunta.db.PMDatabase
import com.example.eduskunta.db.ParliamentMember
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// 30.09.2024 by Arman Yerkeshev 2214297
// Class for loading parliament members from database into application for later use by the UI
class PMViewModel: ViewModel() {
    private val dao = PMDatabase.getInstance().memberDao()
    val members: Flow<List<ParliamentMember>> = dao.getAll()
    var notes by mutableStateOf("")

    fun updateMember(member: ParliamentMember?) {
        GlobalScope.launch {
            if (member != null) {
                dao.update(member)
            }
        }
    }
}