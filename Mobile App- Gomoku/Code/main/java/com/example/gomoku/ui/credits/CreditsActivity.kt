package com.example.gomoku.ui.credits

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gomoku.GomokuDependenciesContainer
import com.example.gomoku.R
import com.example.gomoku.TAG
import com.example.gomoku.ui.error.ErrorActivity
import com.example.gomoku.ui.lobby.LobbyActivity
import com.example.gomoku.ui.profile.ProfileActivity
import com.example.gomoku.ui.ranking.RankingActivity
import com.example.gomoku.ui.savedGames.SavedGamesActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val EMAIL_SUBJECT = "GOMOKU app feedback"

class CreditsActivity : ComponentActivity() {

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    private val viewModel by viewModels<CreditsScreenViewModel> {
        val dependencies = application as GomokuDependenciesContainer
        CreditsScreenViewModel.factory(
            dependencies.informationService
        )
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, CreditsActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.v(TAG, "CreditsActivity.onCreate() called")
        viewModel.fetchAuthors()
        lifecycleScope.launch {
            viewModel.error.collectLatest {
                if (it) {
                    ErrorActivity.navigateTo(this@CreditsActivity)
                }
            }
        }
        setContent {
            CreditsScreen(
                info = viewModel.info,
                onOpenUrlRequested = { openURL(it) },
                onSendEmailRequested = { openSendEmail(it) },
                onLobbyRequest = { LobbyActivity.navigateTo(this) },
                onProfileRequest = { ProfileActivity.navigateTo(this) },
                onRankingRequest = { RankingActivity.navigateTo(this) },
                onSavedGamesRequest = { SavedGamesActivity.navigateTo(this) },
            )
        }
    }

    private fun openURL(uri: Uri) {
        try {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString().removeSurrounding("'", "'")))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Failed to open URL", e)
            Toast
                .makeText(
                    this,
                    R.string.activity_info_no_suitable_app,
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }

    private fun openSendEmail(devMail: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(devMail))
                putExtra(Intent.EXTRA_SUBJECT, arrayOf(EMAIL_SUBJECT))
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Failed to send email", e)
            Toast
                .makeText(
                    this,
                    R.string.activity_info_no_suitable_app,
                    Toast.LENGTH_LONG
                )
                .show()

        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "AboutActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "AboutActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "AboutActivity.onDestroy() called")
    }
}
