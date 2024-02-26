package com.example.gomoku

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.gomoku.http.GameHttp
import com.example.gomoku.http.InformationHttp
import com.example.gomoku.http.LoginHttp
import com.example.gomoku.http.MatchmakingHttp
import com.example.gomoku.http.PlayerHttp
import com.example.gomoku.infrastructure.UserDataStore
import com.google.gson.Gson
import okhttp3.OkHttpClient

const val URI = "https://0489-85-138-152-10.ngrok-free.app"

/**
 * The tag used to identify log messages across the application. Here we elected to use the same
 * tag for all log messages.
 */
const val TAG = "GOMOKU_APP_TAG"

interface GomokuDependenciesContainer {
    val userInfoRepository: UserDataStore
    val playerService: PlayerService
    val loginService: LoginService
    val informationService: InformationService
    val matchmakingService: MatchmakingService
    val gameService: GameService
}

class GomokuApplication : Application(), GomokuDependenciesContainer {
    private val httpClient: OkHttpClient =
        OkHttpClient.Builder()
            .build()

    private val gson: Gson = Gson()

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")
    override val userInfoRepository: UserDataStore
        get() = UserDataStore(dataStore)

    override val playerService: PlayerService
        get() = PlayerHttp(httpClient, gson, URI)

    override val loginService: LoginService
        get() = LoginHttp(httpClient, gson, URI)

    override val informationService: InformationService
        get() = InformationHttp(httpClient, gson, URI)

    override val matchmakingService: MatchmakingService
        get() = MatchmakingHttp(httpClient, gson, URI)

    override val gameService: GameService
        get() = GameHttp(httpClient, gson, URI)
}