package com.example.gomoku.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.gomoku.tags.GameTags

@Composable
fun GameWinPopUp(
    result: String,
    message: String,
    onBackLobby: () -> Unit,
    onSaveGame: (Boolean) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(result)
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = {
                    onBackLobby()
                },
                modifier = Modifier.testTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG)
            ) {
                Text("Back to Lobby")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onSaveGame(true)
                },
                modifier = Modifier.testTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG)
            ) {
                Text("Save Game")
            }
        },
        modifier = Modifier.testTag(GameTags.GAME_WIN_POPUP_TAG)
    )
}