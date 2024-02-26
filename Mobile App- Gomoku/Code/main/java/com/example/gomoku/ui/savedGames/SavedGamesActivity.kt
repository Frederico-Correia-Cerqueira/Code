package com.example.gomoku.ui.savedGames

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.TAG
import com.example.gomoku.ui.credits.CreditsActivity
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity
import com.example.gomoku.ui.profile.ProfileActivity
import com.example.gomoku.ui.ranking.RankingActivity
import com.example.gomoku.ui.replay.ReplayActivity

class SavedGamesActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<SavedGamesScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        SavedGamesScreenViewModel.factory(
            dependencies.gameService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, SavedGamesActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.v(TAG, "SavedGamesActivity.onCreate() called")
        viewModel.fetchSavedGames()
        setContent {
            if (viewModel.error) {
                ErrorActivity.navigateTo(this)
            }
            SavedGamesScreen(
                onSearchRequest = { },
                gameName = viewModel.gameName,
                onGameNameChange = { viewModel.setName(it) },
                savedGames = viewModel.savedGames,
                onRankingRequest = { RankingActivity.navigateTo(this) },
                onProfileRequest = { ProfileActivity.navigateTo(this) },
                onCreditsRequest = { CreditsActivity.navigateTo(this) },
                onLobbyRequest = { LobbyActivity.navigateTo(this) },
                onSavedGameRequest = { gameID, gameName ->
                    viewModel.setGameID(gameID)
                    ReplayActivity.navigateTo(this, viewModel.gameId, gameName)
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "SavedGamesActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "SavedGamesActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "SavedGamesActivity.onDestroy() called")
    }
}