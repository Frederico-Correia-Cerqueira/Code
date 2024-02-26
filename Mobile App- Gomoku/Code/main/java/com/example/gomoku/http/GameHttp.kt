package com.example.gomoku.http


import com.example.gomoku.GameService
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.SaveGameInputModel
import com.example.gomoku.domain.SavedGames
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class GameHttp(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val uri: String
) : GameService {

    private data class Play(val playerID: Int, val l: Int, val c: Int)
    data class Info(val playerID: Int, val info: Boolean)

    private fun requestBodyPlay(playerID: Int, l: Int, c: Int): RequestBody {
        return gson.toJson(Play(playerID, l, c))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun requestBodySwap(playerID: Int, info: Boolean): RequestBody {
        return gson.toJson(Info(playerID, info))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun requestBodySaveGame(nameGame: String, descriptionGame: String): RequestBody {
        return gson.toJson(SaveGameInputModel(nameGame, descriptionGame))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    override suspend fun fetchGetGame(id: Int) =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "games/$id",
            GameDto::class.java
        )

    override suspend fun fetchSavedGames(id: Int): SavedGames =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "games/savedgames/$id",
            SavedGames::class.java
        )

    override suspend fun fetchPlay(id: Int, playerID: Int, l: Int, c: Int) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodyPlay(playerID, l, c),
            "games/$id",
            GameDto::class.java
        )

    override suspend fun fetchSwap(gameID: Int, playerID: Int, info: Boolean) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodySwap(playerID, info),
            "games/$gameID/swap",
            GameDto::class.java
        )

    override suspend fun fetchSwapFirstMove(gameID: Int, playerID: Int, info: Boolean) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodySwap(playerID, info),
            "games/$gameID/swap_first_move",
            GameDto::class.java
        )

    override suspend fun fetchSaveGame(
        id: Int,
        pid: Int,
        nameGame: String,
        descriptionGame: String
    ) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodySaveGame(nameGame, descriptionGame),
            "games/$id/savegame/$pid",
            GameIDDto::class.java
        )

    override suspend fun fetchGetSavedGame(id: Int, pid: Int) =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "games/$id/savegame/$pid",
            GameDto::class.java
        )
}