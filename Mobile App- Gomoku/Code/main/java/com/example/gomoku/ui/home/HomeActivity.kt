package com.example.gomoku.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.TAG
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.ui.lobby.LobbyActivity
import com.example.gomoku.ui.login.LoginActivity


class HomeActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<HomeScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        HomeScreenViewModel.factory(
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, HomeActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.v(TAG, "HomeActivity.onCreate() called")
        viewModel.isLoggedIn()
        setContent {
            HomeScreen(
                onGetStarted = { doNavigation(viewModel.loggedIn.getOrNull()) },
            )
        }
    }

    private fun doNavigation(data: Boolean?) {
        if (data != null) {
            if (!data) {
                LoginActivity.navigateTo(this)
            } else {
                LobbyActivity.navigateTo(this)
            }
        }
    }
}