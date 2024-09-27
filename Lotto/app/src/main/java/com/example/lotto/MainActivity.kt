package com.example.lotto

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.lotto.ui.theme.LottoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LottoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        model = LottoViewModel(),
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

class Lotto(val range: IntRange = 1..40) {
    private val secret = range.toList().shuffled().take(7)
    fun check(guess: List<Int>): Int {
        return guess.intersect(secret).size
    }
}

class LottoViewModel : ViewModel() {
    private val range: IntRange = 1..40
    private val lotto: Lotto = Lotto(range)

    private val _numbers = MutableStateFlow(range.toList())
    var numbers = _numbers.asStateFlow()

    private val _clicked = MutableStateFlow(listOf<Int>())
    var clicked = _clicked.asStateFlow()

    fun updNumbers(numbers: List<Int>) {
        _numbers.value = numbers
    }

    fun updClicked(clicked: List<Int>) {
        _clicked.value = clicked
}

@Composable
fun Greeting(model: LottoViewModel, name: String, modifier: Modifier = Modifier) {
    var result by rememberSaveable { mutableStateOf("") }

    Column {
        Spacer(modifier = Modifier.padding(32.dp))
        Text("You selected: ${model.clicked}")
        Button({
            if (model.clicked.value.size < 7 ) {
                result = "Choose 7 numbers first"
                return@Button
            }


        }) {Text("Check")}
        Text(result)
        Spacer(modifier = Modifier.padding(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 64.dp)
        ) {
            items(model.numbers.value) { n ->
                Log.d("XXX", "item $n")

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    ),
                    onClick = {
                        Log.d("XXX", "$n clicked")
                        if (model.clicked.value.size < 7) {
                            model.updNumbers(model.numbers.value - n)
                            model.updClicked(model.clicked.value + n)
                        }
                        Log.d("XXX", model.numbers.toString())
                    },
                    modifier = Modifier
                        .height(64.dp)
                        .width(64.dp)) {
                        Text("$n")
                    }
                }
            }
        }
    }
}