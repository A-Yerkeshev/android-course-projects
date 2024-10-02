package com.example.eduskunta

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduskunta.PMApplication.Companion.appContext
import com.example.eduskunta.db.DBSynchronizer
import com.example.eduskunta.db.PMDatabase
import com.example.eduskunta.db.ParliamentMember
import com.example.eduskunta.ui.theme.EduskuntaTheme

enum class Screens {
    Info
}

// 30.09.2024 by Arman Yerkeshev 2214297
// Main activity of the application. Sets up the navigation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            EduskuntaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Screens.Info.name) {
                        composable(route = Screens.Info.name) {
                            MemberView(navController, null, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        val db = PMDatabase.getInstance()
        if(db.isOpen) {
            db.openHelper.close()
        }
        super.onDestroy()
    }
}

@Composable
fun MemberView(nav: NavController, hetekaId: Int? = null, modifier: Modifier = Modifier) {
    val vm: PMViewModel = PMViewModel()
    val members: List<ParliamentMember> = vm.members.collectAsState(initial = listOf<ParliamentMember>()).value
    val member: ParliamentMember? = members.find { it.hetekaId == hetekaId } ?: members.randomOrNull()

    Text(
        text = member?.firstname + " " + member?.lastname,
        modifier = modifier
    )
}