package com.example.gomoku.ui.replay

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
import com.example.gomoku.PlayerService
import com.example.gomoku.TAG
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.PlayerInfo
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.launch

class ReplayScreenViewModel(
    private val service: GameService,
    private val playerServices: PlayerService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(
            repository: GameService,
            playerRepository: PlayerService,
            dataStore: UserDataStore
        ) = viewModelFactory {
            initializer { ReplayScreenViewModel(repository, playerRepository, dataStore) }
        }
    }

    var game by mutableStateOf<IOState<GameDto>>(idle())
        private set
    var opponent by mutableStateOf<IOState<UserDto>>(idle())
        private set
    var current by mutableStateOf<PlayerInfo?>(null)
        private set
    var playIdx by mutableIntStateOf(0)
        private set
    var error by mutableStateOf(false)
        private set

    fun incrementIdx() {
        playIdx++
    }

    fun decrementIdx() {
        playIdx--
    }

    fun fetchGame(gameID: Int) {
        if (game !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        game = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                val result = runCatching { service.fetchGetGame(gameID) }
                if (result.isFailure) {
                    error = true
                    Log.e(TAG, "Error fetching game")
                } else {
                    game = loaded(result)
                    val gameDto = game.getOrNull()
                    if (gameDto == null) {
                        error = true
                    } else {
                        val opponentID =
                            if (gameDto.playerB == userInfo.userID) gameDto.playerW else gameDto.playerB
                        current = PlayerInfo(userInfo.userID, userInfo.userName)
                        fetchPlayerInfo(opponentID)
                    }
                }
            }
        }
    }

    private fun fetchPlayerInfo(player: Int) {
        viewModelScope.launch {
            opponent = loading()
            val result = runCatching { playerServices.fetchGetUser(player) }
            if (result.isFailure) {
                error = true
                Log.e(TAG, "Error fetching player")
            } else {
                opponent = loaded(result)
            }
        }
    }
}