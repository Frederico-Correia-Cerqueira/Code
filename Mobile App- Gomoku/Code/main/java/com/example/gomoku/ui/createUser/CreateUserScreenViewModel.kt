package com.example.gomoku.ui.createUser

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.FetchException
import com.example.gomoku.PlayerService
import com.example.gomoku.TAG
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateUserScreenViewModel(
    private val service: PlayerService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(repository: PlayerService, dataStore: UserDataStore) = viewModelFactory {
            initializer { CreateUserScreenViewModel(repository, dataStore) }
        }
    }

    private val _createUserDtoFlow: MutableStateFlow<IOState<CreateUserDto?>> =
        MutableStateFlow(idle())

    /**
     * The flow of states the view model traverses.
     */
    val createUserDto: Flow<IOState<CreateUserDto?>>
        get() = _createUserDtoFlow.asStateFlow()

    var enableToCreateUser by mutableStateOf(true)
        private set
    var userName by mutableStateOf("")
        private set

    var userPassword by mutableStateOf("")
        private set

    var error by mutableIntStateOf(0)
        private set

    fun setName(value: String) {
        userName = value
    }

    fun setPassword(value: String) {
        userPassword = value
    }

    fun fetchCreateUser() {
        if (_createUserDtoFlow.value !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        _createUserDtoFlow.value = loading()
        viewModelScope.launch {
            val result = runCatching { service.fetchCreateUser(userName, userPassword) }
            if (result.isFailure) {
                val e = result.exceptionOrNull()
                if (e is FetchException) {
                    error = e.code
                }
                Log.e(TAG, "Error fetching create user")
            } else {
                val createUserData = result.getOrNull()
                if (createUserData != null) {
                    dataStore.updateUserInfo(
                        DataStoreDto(
                            createUserData.token.toString(),
                            createUserData.userID,
                            userName
                        )
                    )
                }
            }
            _createUserDtoFlow.value = loaded(result)
            Log.v(TAG, "Fetching create user")
        }
    }

    fun resetToIdle() {
        if (_createUserDtoFlow.value !is Loaded) {
            throw IllegalStateException("The view model is not in the loaded state.")
        }
        _createUserDtoFlow.value = idle()
    }
}