package com.example.gomoku.ui.profile

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
import com.example.gomoku.ui.credits.CreditsActivity
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity
import com.example.gomoku.ui.ranking.RankingActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProfileActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }
    private val viewModel by viewModels<ProfileScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        ProfileScreenViewModel.factory(
            dependencies.playerService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, ProfileActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.v(TAG, "CreditsActivity.onCreate() called")
        viewModel.fetchPlayer()
        lifecycleScope.launch {
            viewModel.error.collectLatest {
                if (it) {
                    ErrorActivity.navigateTo(this@ProfileActivity)
                }
            }
        }
        setContent {
            ProfileScreen(
                onRankingRequest = { RankingActivity.navigateTo(this) },
                onSavedGamesRequest = {},
                onLobbyRequest = { LobbyActivity.navigateTo(this) },
                onCreditsRequest = { CreditsActivity.navigateTo(this) },
                playerStats = viewModel.player
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "ProfileActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "ProfileActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "ProfileActivity.onDestroy() called")
    }

}


