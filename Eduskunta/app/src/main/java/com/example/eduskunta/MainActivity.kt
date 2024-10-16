package com.example.eduskunta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduskunta.PMApplication.Companion.appContext
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
        val viewModel = PMViewModel()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            EduskuntaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Screens.Info.name + "/") {
                        composable(route = Screens.Info.name + "/{hetekaId}?") {
                            val hetekaId: Int? = it.arguments?.getString("hetekaId")?.toIntOrNull()
                            viewModel.setMember(hetekaId)
                            MemberView(navController, viewModel, modifier = Modifier.padding(innerPadding))
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
fun MemberView(nav: NavController, viewModel: PMViewModel, modifier: Modifier = Modifier) {
    val member: State<ParliamentMember?> = viewModel.member.collectAsState(initial = null)
    val nextMember: State<ParliamentMember?> = viewModel.nextMember.collectAsState(initial = null)
    val previousMember: State<ParliamentMember?> = viewModel.previousMember.collectAsState(initial = null)
    val image = ImageLoader.getImage(member.value?.pictureUrl)

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
                        nav.navigate(Screens.Info.name + "/${previousMember.value?.hetekaId}")
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
                        nav.navigate(Screens.Info.name + "/${nextMember.value?.hetekaId}")
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
                        contentDescription = "${member.value?.firstname} ${member.value?.lastname}"
                    )
                }
            }
            Text(
                text = "${member.value?.firstname ?:""} ${member.value?.lastname ?: ""} (${member.value?.bornYear ?: ""})",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp, 0.dp)
            )
            Text(
                text = "Party: ${member.value?.party ?: ""}, Constituency: ${member.value?.constituency ?: ""}",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp, 0.dp)
            )
            Text(
                text = "Rating: ${member.value?.rating ?: ""}",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp, 0.dp)
            )
            Text(
                text = "Rate and comment:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp, 16.dp, 16.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                for (i in 1..5) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = COLORS["primary"]!!),
                        onClick = {
                            member.value?.rating = i.toString()
                            viewModel.updateMember(member.value!!)
                        }
                    ) {
                        Text(
                            text = i.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            TextField(
                value = member.value?.notes ?: "",
                modifier = Modifier
                    .padding(16.dp, 0.dp, 16.dp, 0.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle.Default.copy(fontSize = 24.sp),
                onValueChange = {
                    member.value?.notes = it
                    viewModel.updateMember(member.value!!)
                }
            )
        }
    }

}