package com.example.gomoku.ui.savedGames

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.GameService
import com.example.gomoku.TAG
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.SavedGames
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.launch

class SavedGamesScreenViewModel(
    private val service: GameService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(repository: GameService, dataStore: UserDataStore) = viewModelFactory {
            initializer { SavedGamesScreenViewModel(repository, dataStore) }
        }
    }

    var savedGames by mutableStateOf<IOState<SavedGames>>(idle())
        private set
    var gameName by mutableStateOf("")
        private set

    var error by mutableStateOf(false)
        private set

    fun setName(value: String) {
        gameName = value
    }

    var gameId by mutableIntStateOf(-1)
        private set

    fun setGameID(value: Int) {
        gameId = value
    }

    fun fetchSavedGames() {
        if (savedGames !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        savedGames = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                val result = runCatching { service.fetchSavedGames(userInfo.userID) }
                if (result.isFailure) {
                    error = true
                    Log.e(TAG, "Error fetching saved games")
                }
                savedGames = loaded(result)
                Log.v(TAG, "Fetching saved games")
            } else {
                error = true
            }
        }
    }
}