package com.example.gomoku.ui.lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.domain.Loaded
import com.example.gomoku.ui.credits.CreditsActivity
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.login.LoginActivity
import com.example.gomoku.ui.profile.ProfileActivity
import com.example.gomoku.ui.ranking.RankingActivity
import com.example.gomoku.ui.savedGames.SavedGamesActivity
import com.example.gomoku.ui.waitingRoom.WaitingRoomActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val TAG = "LOBBY_APP_TAG"

class LobbyActivity : ComponentActivity() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<LobbyScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        LobbyScreenViewModel.factory(
            dependencies.playerService,
            dependencies.loginService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, LobbyActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        viewModel.fetchPlayer()
        lifecycleScope.launch {
            viewModel.playerState.collectLatest {
                if (it is Loaded) {
                    val state = it.value.getOrNull()
                    if (state != null) {
                        if (state.state != "IDLE") {
                            WaitingRoomActivity.navigateTo(this@LobbyActivity, null)
                        }
                    }
                }
            }
        }
        Log.v(TAG, "LobbyActivity.onCreate() called")
        lifecycleScope.launch {
            viewModel.logoutDto.collectLatest {
                if (it is Loaded) {
                    if (it.value.isSuccess) {
                        LoginActivity.navigateTo(this@LobbyActivity)
                    }
                    viewModel.resetToIdle()
                }
            }
        }
        viewModel.fetchPlayerStats()
        lifecycleScope.launch {
            viewModel.error.collectLatest {
                if (it) {
                    ErrorActivity.navigateTo(this@LobbyActivity)
                }
            }
        }
        setContent {
            LobbyScreen(
                onDefinitionsRequest = { viewModel.setDefinitions() },
                onRankingRequest = { RankingActivity.navigateTo(this) },
                onSavedGamesRequest = { SavedGamesActivity.navigateTo(this) },
                onPlayRequest = { WaitingRoomActivity.navigateTo(this, viewModel.matchmaking) },
                onCreditsRequest = { CreditsActivity.navigateTo(this) },
                onProfileRequest = { ProfileActivity.navigateTo(this) },
                onLogoutRequest = { viewModel.fetchLogout() },
                enableToLogout = viewModel.enableToLogout,
                onGameSelect = viewModel::setGameSelected,
                onGameDetails = viewModel::setDisplayDetails,
                playerStats = viewModel.playerStats,
                selected = viewModel.game,
                definitionPopup = viewModel.definitions,
                displayDetails = viewModel.getDisplayDetails()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "LobbyActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "LobbyActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "LobbyActivity.onDestroy() called")
    }
}
