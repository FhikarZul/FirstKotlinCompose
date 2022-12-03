package com.example.firstcompose

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json

class RemoteSource {
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getPost(): List<Post> {
        val request = httpClient.get("https://jsonplaceholder.typicode.com/posts")

        return when (request.status.value) {
            in 200..299 -> request.body()
            else -> throw  IllegalStateException()
        }
    }
}

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
)

