package com.example.eduskunta

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
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

val COLORS = mapOf(
    "primary" to Color(ContextCompat.getColor(appContext, R.color.primary))
)

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
                    NavHost(navController = navController, startDestination = Screens.Info.name + "/") {
                        composable(route = Screens.Info.name + "/{hetekaId}?") {
                            val hetekaId: Int? = it.arguments?.getString("hetekaId")?.toIntOrNull()
                            MemberView(navController, hetekaId, modifier = Modifier.padding(innerPadding))
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
    val viewModel = PMViewModel()
    val members = viewModel.members.collectAsState(initial = listOf()).value
    var idx: Int = members.indexOfFirst { it.hetekaId == hetekaId }
    if (idx == -1) {
        idx = (0..members.size).random()
    }
    val member: ParliamentMember? = members.getOrNull(idx)
    val image = ImageLoader.getImage(member?.pictureUrl)

    Column(modifier = Modifier.padding(0.dp, 62.dp, 0.dp, 0.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(4.dp, COLORS["primary"]!!),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val prevId: Int? = if (idx == 0) members.first()?.hetekaId else members.getOrNull(idx - 1)?.hetekaId
                        nav.navigate(Screens.Info.name + "/$prevId")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = COLORS["primary"]!!)
                ) {
                    Text(
                        text = "<-",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        val nextId: Int? = members.getOrNull((idx + 1).mod(members.size))?.hetekaId
                        nav.navigate(Screens.Info.name + "/$nextId")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor =
                        Color(ContextCompat.getColor(appContext, R.color.primary)))
                ) {
                    Text(
                        text = "->",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (image != null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        bitmap = image,
                        contentDescription = "${member?.firstname} ${member?.lastname}"
                    )
                }
            }
            Text(
                text = "${member?.firstname ?:""} ${member?.lastname ?: ""} (${member?.bornYear ?: ""})",
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
            Text(
                text = "Party: ${member?.party ?: ""}, Constituency: ${member?.constituency ?: ""}",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }

}