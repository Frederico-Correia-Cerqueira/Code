package com.example.gomoku.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.TAG
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val dataStore: UserDataStore
) : ViewModel() {
    companion object {
        fun factory(
            dataStore: UserDataStore
        ) = viewModelFactory {
            initializer { HomeScreenViewModel(dataStore) }
        }
    }

    var loggedIn by mutableStateOf<IOState<Boolean>>(idle())
        private set

    fun isLoggedIn() {
        if (loggedIn !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        loggedIn = loading()
        viewModelScope.launch {
            val userInfo = dataStore.getUserInfo()
            Log.v(TAG, "DATASTORE: $userInfo")
            loggedIn =
                if (userInfo != null) {
                    loaded(Result.success(true))
                } else {
                    loaded(Result.success(false))
                }
        }
    }
}