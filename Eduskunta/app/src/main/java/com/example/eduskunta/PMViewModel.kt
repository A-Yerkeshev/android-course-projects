package com.example.eduskunta

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.eduskunta.db.PMDatabase
import com.example.eduskunta.db.ParliamentMember
import com.example.eduskunta.db.ParliamentMemberDao
import kotlinx.coroutines.flow.MutableStateFlow

// 30.09.2024 by Arman Yerkeshev 2214297
// Class for loading parliament members from database into application for later use by the UI
class PMViewModel: ViewModel() {
    private val dao = PMDatabase.getInstance().memberDao()
    val members = dao.getAll()
}