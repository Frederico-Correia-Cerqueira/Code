package com.example.gomoku.ui.profile

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
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.domain.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileScreenViewModel(
    private val service: PlayerService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(repository: PlayerService, dataStore: UserDataStore) = viewModelFactory {
            initializer { ProfileScreenViewModel(repository, dataStore) }
        }
    }

    var player by mutableStateOf<IOState<Stats>>(idle())
        private set

    private val _errorFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * The flow of states the view model traverses.
     */
    val error: Flow<Boolean>
        get() = _errorFlow.asStateFlow()


    fun fetchPlayer() {
        if (player !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        player = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            if (userInfo != null) {
                val result = runCatching { service.fetchPlayerStats(userInfo.userID) }
                if (result.isSuccess) {
                    player = loaded(result)
                    Log.v(TAG, "Fetching player stats")
                } else {
                    _errorFlow.value = true
                    Log.e(TAG, "Error fetching player stats")
                }
            } else {
                _errorFlow.value = true
            }
        }
    }
}