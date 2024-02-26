package com.example.gomoku.http

import android.util.Log
import com.example.gomoku.MatchmakingService
import com.example.gomoku.TAG
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.Matchmaking
import com.example.gomoku.domain.MatchmakingDto
import com.example.gomoku.domain.UserDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MatchmakingHttp(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val uri: String
): MatchmakingService {

    private fun requestBodyPlay(matchInfo: Matchmaking): RequestBody {
        return gson.toJson(matchInfo)
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    override suspend fun fetchMatchmaking(matchInfo: Matchmaking): MatchmakingDto {
        Log.v(TAG, "http do fetch Matchmaking state")

        return httpHandler(
            uri,
            client,
            gson,
            requestBodyPlay(matchInfo),
            "matchmaking",
            MatchmakingDto::class.java
        )
    }

    override suspend fun fetchPlayerState(id: Int): UserDto {
        Log.v(TAG, "http do fetch player state")

        return httpHandler(
            uri,
            client,
            gson,
            null,
            "players/$id",
            UserDto::class.java
        )
    }

    override suspend fun fetchGameID(id: Int): GameIDDto {
        Log.v(TAG, "http do fetch game ID state")

        return httpHandler(
            uri,
            client,
            gson,
            null,
            "players/getGameID/$id",
            GameIDDto::class.java
        )
    }
}