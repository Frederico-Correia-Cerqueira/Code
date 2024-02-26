package com.example.gomoku.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.TAG
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.LoginDto
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.ui.createUser.CreateUserActivity
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<LoginScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        LoginScreenViewModel.factory(
            dependencies.loginService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, LoginActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        lifecycleScope.launch {
            viewModel.loginDto.collectLatest {
                if (it is Loaded) {
                    if (it.value.isSuccess) {
                        doNavigation(loginData = it.getOrNull())
                    }
                    viewModel.resetToIdle()
                }
            }
        }
        Log.v(TAG, "LoginActivity.onCreate() called")
        setContent {
            LoginScreen(
                onFetch = { viewModel.fetchLogin() },
                code = viewModel.error,
                onCreateUser = { CreateUserActivity.navigateTo(this) },
                name = viewModel.userName,
                password = viewModel.userPassword,
                onNameChange = { viewModel.setName(it) },
                onPasswordChange = { viewModel.setPassword(it) },
                onNavigateError = { ErrorActivity.navigateTo(this) },
                enableToLogin = viewModel.enableToLogin
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "LoginActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "LoginActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "LoginActivity.onDestroy() called")
    }

    private fun doNavigation(loginData: LoginDto?) {
        if (loginData == null)
            navigateTo(this)
        else
            LobbyActivity.navigateTo(this)
    }
}