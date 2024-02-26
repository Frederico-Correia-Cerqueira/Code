package com.example.gomoku.ui.game

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.FetchException
import com.example.gomoku.GameService
import com.example.gomoku.PlayerService
import com.example.gomoku.TAG
import com.example.gomoku.domain.BoardTypes
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.OpeningRules
import com.example.gomoku.domain.Variants
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.domain.PlayerInfo
import com.example.gomoku.domain.UserDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameScreenViewModel(
    private val service: GameService,
    private val playerService: PlayerService,
    private val dataStore: UserDataStore
) : ViewModel() {
    companion object {
        fun factory(
            repository: GameService,
            playerRepository: PlayerService,
            dataStore: UserDataStore
        ) = viewModelFactory {
            initializer { GameScreenViewModel(repository, playerRepository, dataStore) }
        }
    }

    var game by mutableStateOf<IOState<GameDto>>(idle())
        private set
    var opponent by mutableStateOf<IOState<UserDto>>(idle())
        private set
    var current by mutableStateOf<PlayerInfo?>(null)
        private set

    var enableToPlay by mutableStateOf(true)
        private set

    var gameName by mutableStateOf("")
        private set
    var gameDescription by mutableStateOf("")
        private set

    var saveGame by mutableStateOf(false)
        private set

    var error by mutableStateOf(false)
        private set

    fun setName(value: String) {
        gameName = value
    }

    fun setDescription(value: String) {
        gameDescription = value
    }

    fun setSave(value: Boolean) {
        saveGame = value
    }

    fun fetchGame(gameID: Int) {
        Log.v(TAG, "Fetching game ID")
        viewModelScope.launch {
            game = loading()
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                val result = runCatching { service.fetchGetGame(gameID) }
                if (result.isFailure) {
                    error = true
                } else {
                    error = false
                    game = loaded(result)
                    val gameDto = game.getOrNull()
                    if (gameDto == null) {
                        error = true
                    } else {
                        val opponentID =
                            if (gameDto.playerB == userInfo.userID) gameDto.playerW else gameDto.playerB
                        current = PlayerInfo(userInfo.userID, userInfo.userName)
                        fetchPlayerInfo(opponentID)
                        startPollingForTurn(userInfo.userID, gameID)
                    }
                }
            }
        }
    }

    private fun startPollingForTurn(playerId: Int, gameID: Int) {
        viewModelScope.launch {
            val gameDto = game.getOrNull()
            if (gameDto != null) {
                while (gameDto.board.turn != playerId) {
                    try {
                        if (gameDto.board.type == BoardTypes.BOARD_WIN) {
                            break
                        }
                        val result = runCatching { service.fetchGetGame(gameID) }
                        if (result.isFailure) {
                            error = true
                            Log.e(TAG, "Error fetching game")
                        } else {
                            error = false
                            game = loaded(result)
                        }
                        val newGame = result.getOrNull()

                        if (newGame != null) {
                            if (newGame.board.turn == playerId) {
                                return@launch
                            }
                        }

                        Log.v(TAG, "Fetching polling")
                        delay(5000)
                    } catch (e: FetchException) {
                        Log.e(TAG, "Error fetching polling")
                    }
                }
            }

        }
    }

    fun fetchPlay(column: Int, row: Int, gameID: Int) {

        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                if (game.getOrNull()?.board?.turn != userInfo.userID) {
                    return@launch
                }
                enableToPlay = false
                val result =
                    runCatching { service.fetchPlay(gameID, userInfo.userID, column, row) }
                if (result.isFailure) {
                    error = true
                    Log.e(TAG, "Error fetching player stats")
                } else {
                    error = false
                    game = loaded(result)
                }
                enableToPlay = true
                startPollingForTurn(userInfo.userID, gameID)
                Log.v(TAG, "Fetching play")
            } else {
                error = true
            }
        }
    }

    fun fetchSwap(gameID: Int, inf: Boolean) {
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                if (game.getOrNull()?.openingRules == OpeningRules.SWAP) {
                    val result = runCatching { service.fetchSwap(gameID, userInfo.userID, inf) }
                    if (result.isFailure) {
                        error = true
                        Log.e(TAG, "Error fetching player stats")
                    } else {
                        game = loaded(result)
                    }
                }
                if (game.getOrNull()?.variants == Variants.SWAP_FIRST_MOVE) {
                    val result =
                        runCatching { service.fetchSwapFirstMove(gameID, userInfo.userID, inf) }
                    if (result.isFailure) {
                        error = true
                        Log.e(TAG, "Error fetching player stats")
                    } else {
                        game = loaded(result)
                        error = false
                    }
                }
                startPollingForTurn(userInfo.userID, gameID)
                Log.v(TAG, "Fetching swap")
            } else {
                error = true
            }
        }
    }

    fun fetchSaveGame(gameID: Int) {
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                runCatching {
                    service.fetchSaveGame(
                        gameID,
                        userInfo.userID,
                        gameName,
                        gameDescription
                    )
                }
                Log.v(TAG, "Fetching save game")
            }
        }
    }

    private fun fetchPlayerInfo(player: Int) {
        viewModelScope.launch {
            opponent = loading()
            val result = runCatching { playerService.fetchGetUser(player) }
            if (result.isFailure) {
                error = true
                Log.e(TAG, "Error fetching player")
            } else {
                opponent = loaded(result)
            }
            Log.v(TAG, "Fetching player")
        }
    }
}


