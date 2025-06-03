package com.example.counterapp.counter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.counterapp.counter.presentation.model.CounterAction
import com.example.counterapp.ui.theme.CounterAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CounterAppTheme {

                LaunchedEffect(Unit) {
                    viewModel.setAction(CounterAction.Init)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Counter(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Counter(
    viewModel: CounterViewModel,
    modifier: Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Counter: ${uiState.content?.count ?: "Loading..."}") // Handle null content if needed

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        if (uiState.showFullScreenError) { // Or however you model errors
            Text("An error occurred!")
        }

        Button(onClick = { viewModel.setAction(CounterAction.Increment) }) {
            Text("Increment")
        }
        Button(onClick = { viewModel.setAction(CounterAction.Decrement) }) {
            Text("Decrement")
        }
    }
}
