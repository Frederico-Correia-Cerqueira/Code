package com.example.gomoku.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.gomoku.tags.GameTags

@Composable
fun SwapPopUp(onDismissRequest: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismissRequest(false) },
        text = {
            Text("Do you want to swap?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest(true)
                },
                modifier = Modifier.testTag(GameTags.GAME_SWAP_POPUP_YES_BUTTON_TAG)
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest(false)
                },
                modifier = Modifier.testTag(GameTags.GAME_SWAP_POPUP_NO_BUTTON_TAG)
            ) {
                Text("No")
            }
        },
        modifier = Modifier.testTag(GameTags.GAME_SWAP_POPUP_TAG)
    )
}

@Preview
@Composable
fun PreviewSwapPopUp() {
    Column {
        SwapPopUp(onDismissRequest = {})
    }
}
