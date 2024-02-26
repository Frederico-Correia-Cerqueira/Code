package com.example.gomoku.ui.ranking

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
import com.example.gomoku.ui.profile.ProfileActivity
import com.example.gomoku.ui.savedGames.SavedGamesActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RankingActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<RankingScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        RankingScreenViewModel.factory(
            dependencies.playerService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, RankingActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.v(TAG, "RankingActivity.onCreate() called")
        viewModel.getPlayerName()
        viewModel.fetchStats()
        lifecycleScope.launch {
            viewModel.error.collectLatest {
                if (it) {
                    ErrorActivity.navigateTo(this@RankingActivity)
                }
            }
        }
        setContent {
            RankingScreen(
                stats = viewModel.stats,
                player = viewModel.player,
                rank = viewModel.ranking,
                onSearchByName = {
                    viewModel.setPlayers(it)
                },
                onGetPlayerStats = {
                    viewModel.setPlayers(viewModel.playerName)
                },
                onChooseRanking = { viewModel.setRank(it) },
                onProfileRequest = { ProfileActivity.navigateTo(this) },
                onLobbyRequest = { LobbyActivity.navigateTo(this) },
                onCreditsRequest = { CreditsActivity.navigateTo(this) },
                onSavedGamesRequest = { SavedGamesActivity.navigateTo(this) },
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "RankingActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "RankingActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "RankingActivity.onDestroy() called")
    }
}