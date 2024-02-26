package com.example.gomoku.http


import com.example.gomoku.PlayerService
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.domain.Ranking
import com.example.gomoku.domain.Stats
import com.example.gomoku.domain.UserDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PlayerHttp(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val uri: String
) : PlayerService {

    private data class Form(val name: String, val password: String)

    private fun requestBodyForm(name: String, password: String): RequestBody {
        return gson.toJson(Form(name, password))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    override suspend fun fetchGetUser(id: Int) =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "players/$id",
            UserDto::class.java
        )

    override suspend fun fetchPlayerStats(id: Int) =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "players/stats/$id",
            Stats::class.java
        )

    override suspend fun fetchCreateUser(name: String, password: String) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodyForm(name, password),
            "players",
            CreateUserDto::class.java
        )

    override suspend fun fetchStats() =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "stats",
            Ranking::class.java
        )
}

