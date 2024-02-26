package com.example.gomoku.ui.waitingRoom


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.TAG
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Matchmaking
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.game.GameActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

private const val MATCHMAKING_EXTRA = "Matchmaking"

class WaitingRoomActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<WaitingRoomScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        WaitingRoomScreenViewModel.factory(
            dependencies.matchmakingService, dependencies.userInfoRepository
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity, matchmaking: Matchmaking?) {
            with(origin) {
                val intent = Intent(origin, WaitingRoomActivity::class.java)
                if (matchmaking != null) {
                    intent.putExtra(MATCHMAKING_EXTRA, MatchmakingExtra(matchmaking))
                } else {
                    intent.putExtra(MATCHMAKING_EXTRA, MatchmakingExtra(null, null, null))
                }
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        lifecycleScope.launch {
            viewModel.gameID.collectLatest {
                if (it is Loaded) {
                    if (it.value.isSuccess) {
                        val result = it.getOrNull()
                        if (result != null) {
                            doNavigation(gameID = result)
                        }
                    }
                    viewModel.resetToIdle()
                }
            }
        }
        Log.v(TAG, "WaitingRoomActivity.onCreate() called")
        getMatchmakingExtra()?.let { matchmakingExtra ->
            Log.e(TAG, "Matchmaking extra: ${matchmakingExtra.playerID}")
            if (matchmakingExtra.playerID != null) {
                viewModel.fetchMatchmaking(matchmakingExtra.toMatchmaking())
            }
            viewModel.startPollingForWaitingRoom()

        }
        lifecycleScope.launch {
            viewModel.error.collectLatest { if (it) ErrorActivity.navigateTo(this@WaitingRoomActivity) }
        }
        setContent {
            WaitingRoomScreen()
        }
    }

    /**
     * Helper method to get the matchmaking extra from the intent.
     */
    @Suppress("DEPRECATION")
    private fun getMatchmakingExtra(): MatchmakingExtra? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(
            MATCHMAKING_EXTRA,
            MatchmakingExtra::class.java
        )
        else intent.getParcelableExtra(MATCHMAKING_EXTRA)

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "WaitingRoomActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "WaitingRoomActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "WaitingRoomActivity.onDestroy() called")
    }

    /**
     * Navigates to the appropriate activity, depending on whether the
     * game ID has already been provided or not.
     * @param gameID the game ID.
     */
    private fun doNavigation(gameID: GameIDDto) {
        Log.v(TAG, "Navigating to game activity: ${gameID.id}")
        GameActivity.navigateTo(this, gameID.id)
    }
}

/**
 * Represents the data to be passed as an extra in the intent that navigates to the
 * [WaitingRoomActivity] activity. We use this class because the [Matchmaking] class is not
 * parcelable and we do not to make it parcelable because it is a domain class.
 *
 * @property playerID the player's ID
 * @property gameType the game type
 * @property openingRules the game opening rules
 **/
@Parcelize
private data class MatchmakingExtra(
    val playerID: Int?, val gameType: String?, val openingRules: String?
) : Parcelable {
    constructor(matchmaking: Matchmaking) : this(
        matchmaking.playerID, matchmaking.gameType, matchmaking.openingRules
    )
}

/**
 * Helper method to convert a [MatchmakingExtra] to a [Matchmaking]
 **/
private fun MatchmakingExtra.toMatchmaking() = Matchmaking(playerID, gameType, openingRules)