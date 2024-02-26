package com.example.gomoku.ui.createUser

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
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity
import com.example.gomoku.ui.login.LoginActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateUserActivity : ComponentActivity() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<CreateUserScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        CreateUserScreenViewModel.factory(
            dependencies.playerService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, CreateUserActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        lifecycleScope.launch {
            viewModel.createUserDto.collectLatest {
                if (it is Loaded) {
                    if (it.value.isSuccess) {
                        doNavigation(createUserData = it.getOrNull())
                    }
                    viewModel.resetToIdle()
                }
            }
        }
        Log.v(TAG, "CreateUserActivity.onCreate() called")
        setContent {
            CreateUserScreen(
                onFetch = { viewModel.fetchCreateUser() },
                code = viewModel.error,
                onCreateUser = { LoginActivity.navigateTo(this) },
                name = viewModel.userName,
                password = viewModel.userPassword,
                enableToCreateUser = viewModel.enableToCreateUser,
                onNameChange = { viewModel.setName(it) },
                onPasswordChange = { viewModel.setPassword(it) },
                onNavigateError = { ErrorActivity.navigateTo(this) }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "CreateUserActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "CreateUserActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "CreateUserActivity.onDestroy() called")
    }

    private fun doNavigation(createUserData: CreateUserDto?) {
        if (createUserData == null)
            navigateTo(this)
        else
            LobbyActivity.navigateTo(this)
    }
}