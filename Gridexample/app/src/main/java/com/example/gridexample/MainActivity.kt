package com.example.gridexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gridexample.ui.theme.GridExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GridExampleTheme {
                TopicApp()
            }
        }
    }
}

@Composable
fun TopicApp() {

}

@Composable
fun TopicCard(m: Topic) {
    Card() {
        Image(
            painter = painterResource(m.iconResId),
            contentDescription = stringResource(m.stringResId),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            contentScale = ContentScale.Crop
        )
        Text(LocalContext.current.getString(m.stringResId))
        Text(m.paricipants.toString())
    }
}

@Preview
@Composable
private fun TopicCardPreview() {
    TopicCard(DataSource.topics.first())
}