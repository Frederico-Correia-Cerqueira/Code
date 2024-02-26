package com.example.gomoku.ui.ranking

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.PlayerService
import com.example.gomoku.TAG
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RankingScreenViewModel(
    private val service: PlayerService,
    private val dataStore: UserDataStore
) : ViewModel() {
    companion object {
        fun factory(repository: PlayerService, dataStore: UserDataStore) = viewModelFactory {
            initializer { RankingScreenViewModel(repository, dataStore) }
        }
    }

    var stats by mutableStateOf<IOState<List<Stats>>>(idle())
        private set

    var ranking by mutableStateOf<Rank?>(null)
        private set

    var playerName by mutableStateOf("")
        private set

    var player by mutableStateOf("")
        private set

    fun setPlayers(value: String) {
        player = value
    }

    fun setRank(value: Rank?) {
        ranking = value
    }

    private val _errorFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * The flow of states the view model traverses.
     */
    val error: Flow<Boolean>
        get() = _errorFlow.asStateFlow()


    fun fetchStats() {
        if (stats !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        stats = loading()
        viewModelScope.launch {
            val result = runCatching { service.fetchStats().stats }
            if (result.isSuccess) {
                stats = loaded(result)
                Log.v(TAG, "Fetching ranking")
            } else {
                _errorFlow.value = true
                Log.e(TAG, "Error fetching player stats")
            }

        }
    }

    fun getPlayerName() {
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                playerName = userInfo.userName
            }
        }
    }
}