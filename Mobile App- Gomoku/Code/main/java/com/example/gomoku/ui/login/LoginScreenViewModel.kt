package com.example.gomoku.ui.login

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
import com.example.gomoku.LoginService
import com.example.gomoku.TAG
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.LoginDto
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val service: LoginService,
    private val dataStore: UserDataStore
) : ViewModel() {

    companion object {
        fun factory(
            repository: LoginService,
            dataStore: UserDataStore,
        ) = viewModelFactory {
            initializer { LoginScreenViewModel(repository, dataStore) }
        }
    }

    private val _loginDtoFlow: MutableStateFlow<IOState<LoginDto?>> = MutableStateFlow(idle())

    /**
     * The flow of states the view model traverses.
     */
    val loginDto: Flow<IOState<LoginDto?>>
        get() = _loginDtoFlow.asStateFlow()

    var enableToLogin by mutableStateOf(true)
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

    fun fetchLogin() {
        if (_loginDtoFlow.value !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        _loginDtoFlow.value = loading()
        viewModelScope.launch {
            enableToLogin = false
            val result = runCatching { service.fetchLogin(userName, userPassword) }
            if (result.isFailure) {
                val e = result.exceptionOrNull()
                if (e is FetchException) {
                    enableToLogin = true
                    error = e.code
                }
                Log.e(TAG, "Error fetching login")
            } else {
                val login = result.getOrNull()
                if (login != null) {
                    dataStore.updateUserInfo(DataStoreDto(login.token, login.userID, userName))
                }
            }
            _loginDtoFlow.value = loaded(result)
            Log.v(TAG, "Fetching login - $result")
        }
    }

    fun resetToIdle() {
        if (_loginDtoFlow.value !is Loaded) {
            throw IllegalStateException("The view model is not in the loaded state.")
        }
        _loginDtoFlow.value = idle()
    }
}