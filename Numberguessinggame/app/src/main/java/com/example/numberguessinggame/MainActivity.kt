package com.example.numberguessinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numberguessinggame.ui.theme.NumberGuessingGameTheme
import com.example.numberguessinggame.NumberGame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NumberGuessingGameTheme {
                UI()
            }
        }
    }
}

val game = NumberGame(1..10)

@Composable
fun UI(modifier: Modifier = Modifier) {
    var guess: Int? by remember { mutableStateOf(null) }
    val titles = mapOf(
        "starter" to stringResource(R.string.range, game.range.first, game.range.last),
        "invalid" to stringResource(R.string.invalid_input, game.range.first, game.range.last),
        GuessResult.HIGH to stringResource(R.string.high),
        GuessResult.LOW to stringResource(R.string.low),
        GuessResult.HIT to stringResource(R.string.hit)
    )

    var title by remember { mutableStateOf(titles["starter"]) }

    Column(
        modifier = Modifier.padding(60.dp)
    ) {
        Text(title ?: "")
        Spacer(modifier = Modifier.size(24.dp))
        TextField(
            value = guess?.toString() ?: "",
            onValueChange = { guess = it.toIntOrNull() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.input_number)) }
        )
        Button(
            onClick = {
                if (guess == null || guess !in game.range) {
                    title = titles["invalid"]
                    return@Button
                }

                title = titles[game.makeGuess(guess!!)]
            },
            content = { Text(stringResource(R.string.ok)) }
        )
    }
}



@Preview(showBackground = true)
@Composable
fun UIPreview() {
    NumberGuessingGameTheme {
        UI()
    }
}