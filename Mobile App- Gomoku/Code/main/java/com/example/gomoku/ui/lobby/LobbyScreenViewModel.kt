package com.example.gomoku.ui.lobby

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.FetchException
import com.example.gomoku.LoginService
import com.example.gomoku.PlayerService
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.LogoutDto
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.domain.Matchmaking
import com.example.gomoku.domain.Stats
import com.example.gomoku.domain.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Options(val description: String) {
    NORMAL("Players alternate turns placing a stone of their color on an empty intersection."),
    SWAP("The first player places three stones (two black, and one white) anywhere on the board. The second player then chooses which color to play as. Play proceeds from there as normal with white playing their second stone."),
    SWAP_FIRST_MOVE("Once the first player places a black stone on the board, the second player has the right to swap colors. The rest of the game proceeds as normal gomoku game.") {
        override fun toString(): String {
            // Replaces '_' with ' '
            return super.toString().replace("_", " ")
        }
    }
}

class LobbyScreenViewModel(
    private val service: PlayerService,
    private val authService: LoginService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(
            repository: PlayerService,
            authService: LoginService,
            dataStore: UserDataStore
        ) = viewModelFactory {
            initializer { LobbyScreenViewModel(repository, authService, dataStore) }
        }
    }

    private var displayedDetails by mutableStateOf(
        mapOf(
            Options.NORMAL to false,
            Options.SWAP to false,
            Options.SWAP_FIRST_MOVE to false
        )
    )

    private val _logoutDtoFlow: MutableStateFlow<IOState<LogoutDto?>> = MutableStateFlow(idle())

    /**
     * The flow of states the view model traverses.
     */
    val logoutDto: Flow<IOState<LogoutDto?>>
        get() = _logoutDtoFlow.asStateFlow()

    var game: Options by mutableStateOf(Options.NORMAL)
        private set

    var enableToLogout by mutableStateOf(true)
        private set
    var matchmaking: Matchmaking by mutableStateOf(Matchmaking(-1, "NORMAL", "NORMAL"))
        private set

    var playerStats by mutableStateOf<IOState<Stats>>(idle())
        private set

    private val _errorFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * The flow of states the view model traverses.
     */
    val error: Flow<Boolean>
        get() = _errorFlow.asStateFlow()


    private val _playerStateFlow: MutableStateFlow<IOState<UserDto>> = MutableStateFlow(idle())

    /**
     * The flow of states the view model traverses.
     */
    val playerState: Flow<IOState<UserDto>>
        get() = _playerStateFlow.asStateFlow()


    var definitions by mutableStateOf(false)
        private set

    fun setDefinitions() {
        definitions = !definitions
    }

    /**
     * Change the displayed details of the game options
     */
    fun setDisplayDetails(options: Options) {
        displayedDetails = displayedDetails.map { (key, value) ->
            if (key == options) {
                key to !value
            } else {
                key to false
            }
        }.toMap()
    }

    fun getDisplayDetails(): String? {
        val option =
            displayedDetails.keys.firstOrNull { displayedDetails[it] == true } ?: return null
        return option.description
    }

    fun setGameSelected(option: Options) {
        game = option
        parseOptionToMatchmaking()
    }

    fun fetchPlayer() {
        viewModelScope.launch {
            _playerStateFlow.value = loading()
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                val result = runCatching { service.fetchGetUser(userInfo.userID) }
                if (result.isSuccess) {
                    _playerStateFlow.value = loaded(result)
                    Log.v(TAG, "fetching Authors")
                } else {
                    _errorFlow.value = true
                    Log.e(TAG, "Error fetching player stats")
                }
            }
        }

    }

    fun fetchPlayerStats() {
        if (playerStats !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        playerStats = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                matchmaking = matchmaking.copy(playerID = userInfo.userID)
                val result = runCatching { service.fetchPlayerStats(userInfo.userID) }
                if (result.isSuccess) {
                    playerStats = loaded(result)
                    Log.v(TAG, "fetching Authors")
                } else {
                    _errorFlow.value = true
                    Log.e(TAG, "Error fetching player stats")
                }
            }
        }
    }

    fun fetchLogout() {
        if (_logoutDtoFlow.value !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }

        _logoutDtoFlow.value = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                enableToLogout = false
                val result = runCatching { authService.fetchLogout(userInfo.token) }
                if (result.isFailure) {
                    val e = result.exceptionOrNull()
                    if (e is FetchException) {
                        _errorFlow.value = true
                        enableToLogout = false
                    }
                    Log.e(TAG, "Error fetching logout")
                } else {
                    dataStore.removeUserInfo()
                }
                _logoutDtoFlow.value = loaded(result)
                Log.v(com.example.gomoku.TAG, "Fetching logout - $result")
            } else {
                _errorFlow.value = true
            }
        }

    }

    private fun parseOptionToMatchmaking() {
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                matchmaking = when (game) {
                    Options.NORMAL ->
                        Matchmaking(userInfo.userID, "NORMAL", "NORMAL")

                    Options.SWAP -> Matchmaking(userInfo.userID, "NORMAL", "SWAP")
                    Options.SWAP_FIRST_MOVE -> Matchmaking(
                        userInfo.userID, gameType = "SWAP_FIRST_MOVE", openingRules = "NORMAL"
                    )
                }

            }
        }
    }

    fun resetToIdle() {
        if (_logoutDtoFlow.value !is Loaded) {
            throw IllegalStateException("The view model is not in the loaded state.")
        }
        _logoutDtoFlow.value = idle()
    }
}