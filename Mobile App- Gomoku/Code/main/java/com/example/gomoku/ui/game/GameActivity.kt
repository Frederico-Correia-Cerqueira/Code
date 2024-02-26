package com.example.gomoku.ui.game

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
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity

private const val GAME_ID = "GameID"
private const val FALLBACK_VALUE = -1

class GameActivity : ComponentActivity() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<GameScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        GameScreenViewModel.factory(
            dependencies.gameService,
            dependencies.playerService,
            dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: Context, gameID: Int) {
            origin.startActivity(createIntent(origin, gameID))
        }

        private fun createIntent(origin: Context, gameID: Int): Intent {
            val intent = Intent(origin, GameActivity::class.java)
            intent.putExtra(GAME_ID, gameID)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        val gameID: Int = intent.getIntExtra(GAME_ID, FALLBACK_VALUE)
        Log.v(TAG, "GameActivity.onCreate() called")
        viewModel.fetchGame(gameID)
        setContent {
            if (viewModel.error) {
                ErrorActivity.navigateTo(this)
            }

            GameScreen(
                viewModel.game,
                viewModel.current,
                viewModel.opponent,
                viewModel.enableToPlay,
                { column, row -> viewModel.fetchPlay(column, row, gameID) },
                { info -> viewModel.fetchSwap(gameID, info) },
                { LobbyActivity.navigateTo(this) },
                viewModel.gameName,
                viewModel.gameDescription,
                viewModel::setName,
                viewModel::setDescription,
                viewModel.saveGame,
                viewModel::setSave,
                { viewModel.fetchSaveGame(gameID) }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "GameActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "GameActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "GameActivity.onDestroy() called")
    }
}