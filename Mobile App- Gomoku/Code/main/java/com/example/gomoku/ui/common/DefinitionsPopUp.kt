package com.example.gomoku.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import com.example.gomoku.tags.LobbyTags
import com.example.gomoku.ui.replay.ScreenSizes

@Composable
fun DefinitionsPopUp(
    screenSizes: ScreenSizes,
    enableToLogout: Boolean,
    onBackRequest: () -> Unit,
    onLogoutRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onBackRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LobbyTags.DEFINITIONS_CARD_TAG),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(screenSizes.width * 0.5f)
                )

                Text("Choose an option")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            onBackRequest()
                        },
                        modifier = Modifier.testTag(LobbyTags.GO_BACK_REQUEST_TAG)
                    ) {
                        Row {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                            Text("Back")

                        }
                    }

                    TextButton(
                        onClick = {
                            onLogoutRequest()
                        },
                        enabled = enableToLogout,
                        modifier = Modifier.testTag(LobbyTags.LOGOUT_REQUEST_TAG)
                    ) {
                        Row {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}