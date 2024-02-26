package com.example.gomoku.ui.replay

import android.content.Context
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
import com.example.gomoku.ui.savedGames.SavedGamesActivity

private const val GAME_ID_EXTRA = "GameID"
private const val FALLBACK_VALUE = -1
private const val GAME_NAME_EXTRA = "GameName"

class ReplayActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<ReplayScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        ReplayScreenViewModel.factory(
            dependencies.gameService,
            dependencies.playerService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: Context, gameID: Int, gameName: String) {
            origin.startActivity(createIntent(origin, gameID, gameName))
        }

        private fun createIntent(origin: Context, gameID: Int, gameName: String): Intent {
            val intent = Intent(origin, ReplayActivity::class.java)
            intent.putExtra(GAME_ID_EXTRA, gameID)
            intent.putExtra(GAME_NAME_EXTRA, gameName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        val gameID = intent.getIntExtra(GAME_ID_EXTRA, FALLBACK_VALUE)
        val gameName = intent.getStringExtra(GAME_NAME_EXTRA)
        Log.v(TAG, "GameName = $gameName")
        Log.v(TAG, "ReplayActivity.onCreate() called")
        viewModel.fetchGame(gameID)
        setContent {
            if (viewModel.error) {
                ErrorActivity.navigateTo(this)
            }
            ReplayScreen(
                gameName = gameName,
                game = viewModel.game,
                current = viewModel.current,
                opponent = viewModel.opponent,
                playIdx = viewModel.playIdx,
                onIncrementIdx = { viewModel.incrementIdx() },
                onDecrementIdx = { viewModel.decrementIdx() },
                onRankingRequest = { RankingActivity.navigateTo(this) },
                onSavedGamesRequest = { SavedGamesActivity.navigateTo(this) },
                onLobbyRequest = { LobbyActivity.navigateTo(this) },
                onCreditsRequest = { CreditsActivity.navigateTo(this) },
                onProfileRequest = { ProfileActivity.navigateTo(this) }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "ReplayActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "ReplayActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "ReplayActivity.onDestroy() called")
    }
}