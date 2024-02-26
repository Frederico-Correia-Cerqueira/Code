package com.example.gomoku.ui.waitingRoom

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.MatchmakingService
import com.example.gomoku.TAG
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Matchmaking
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the WaitingRoom Screen.
 * @param service The service for the WaitingRoom information
 */
class WaitingRoomScreenViewModel(
    private val service: MatchmakingService,
    private val dataStore: UserDataStore
) : ViewModel() {
    companion object {
        fun factory(service: MatchmakingService, dataStore: UserDataStore) = viewModelFactory {
            initializer { WaitingRoomScreenViewModel(service, dataStore) }
        }
    }

    private val _gameIDFlow: MutableStateFlow<IOState<GameIDDto>> = MutableStateFlow(idle())

    /**
     * The flow of states the view model traverses.
     */
    val gameID: Flow<IOState<GameIDDto>>
        get() = _gameIDFlow.asStateFlow()

    private var playerState by mutableStateOf<IOState<String>>(idle())

    private val _errorFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * The flow of states the view model traverses.
     */
    val error: Flow<Boolean>
        get() = _errorFlow.asStateFlow()


    /**
     * Initiates the matchmaking process and updates the view state accordingly.
     * Handles loading state transition and updates _gameIDFlow based on the fetched result.
     * @throws IllegalStateException if the view model is not in the idle state.
     */
    fun fetchMatchmaking(matchMaking: Matchmaking) {
        viewModelScope.launch {
            val result = runCatching { service.fetchMatchmaking(matchMaking) }
            if (result.isFailure) {
                Log.e(TAG, "Error fetching matchmaking")
            }
        }
    }

    /**
     * Initiates polling for the waiting room until the player state becomes "IN_GAME".
     */
    fun startPollingForWaitingRoom() {
        if (playerState !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        playerState = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                while (playerState.getOrNull() != "IN_GAME") {
                    val result = runCatching { service.fetchPlayerState(userInfo.userID).state }
                    if (result.isSuccess) {
                        playerState = loaded(result)
                        if (playerState.getOrNull() == "IN_GAME") {
                            break
                        }
                        Log.v(TAG, "Fetching polling")
                        delay(5000)
                    } else {
                        _errorFlow.value = true
                        Log.e(TAG, "Error fetching player stats")
                    }
                }
                fetchGameID(userInfo.userID)
            }
        }
    }


    /**
     * Initiates the retrieval of a game ID and updates the view model state accordingly.
     * Handles loading state transition and updates _gameIDFlow based on the fetched result.
     * @throws IllegalStateException if the view model is not in the idle state.
     */
    private fun fetchGameID(playerID: Int) {
        if (_gameIDFlow.value !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        _gameIDFlow.value = loading()
        viewModelScope.launch {
            Log.v(TAG, "Fetching game ID")
            val result = runCatching { service.fetchGameID(playerID) }
            if (result.isSuccess) {
                _gameIDFlow.value = loaded(result)
            } else {
                _errorFlow.value = true
                Log.e(TAG, "Error fetching game ID")
            }
        }

    }

    /**
     * Resets the view model to the idle state. From the idle state, the game ID
     * can be updated.
     * @throws IllegalStateException if the view model is not in the Loaded state.
     */
    fun resetToIdle() {
        if (_gameIDFlow.value !is Loaded) {
            throw IllegalStateException("The view model is not in the loaded state.")
        }
        _gameIDFlow.value = idle()
    }
}
