package com.example.firstcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firstcompose.ui.theme.FirstComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: NumberViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    Column(Modifier.verticalScroll(state = rememberScrollState())){
                        uiState.posts.forEach { post ->
                            RowPostsView(
                                title = post.title,
                                body = post.body
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowPostsView(title: String, body: String){
    Card(Modifier.padding(10.dp)){
        Column(Modifier.padding(5.dp)){
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Divider(Modifier.padding(vertical = 5.dp))
            Text(text = body)
        }
    }
}

class NumberViewModel : ViewModel() {
    private val remoteSource = RemoteSource()
    val uiState = MutableStateFlow(UiState())

    init {
        getData()
    }

   private fun getData() {
        viewModelScope.launch {
            // UI THREAD
            uiState.update { it.copy(posts = fetchRemoteCount()) }
            // UPDATE VALUE IN UI THREAD
        }
    }

    private suspend fun fetchRemoteCount(): List<Post> = withContext(Dispatchers.IO) {
        // SWITCH TO IO THREAD
        remoteSource.getPost()
    } // RETURN TO UI THREAD

    data class UiState(
        val posts: List<Post> = ArrayList()
    )
}