package com.example.kumamon.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState = viewModel.uiState.collectAsState()
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val state = uiState.value) {
                        is Result.Success<List<Chat>> -> {
                            printList(state.value)
                            Conversation(messages = state.value) { submission ->
                                viewModel.onSubmit(submission)
                            }
                        }
                        is Result.Failure -> { }
                        is Result.Loading -> { }
                    }
                }
            }
        }
    }

    private fun printList(chats: List<Chat>) {
        val sb = StringBuilder("")
        for(i in chats.indices) {
            sb.append(chats[i].message)
            if (i != chats.size-1) {
                sb.append(", ")
            }
        }
        Log.d("TRACE", "observe conversation=${sb.toString()}")
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
