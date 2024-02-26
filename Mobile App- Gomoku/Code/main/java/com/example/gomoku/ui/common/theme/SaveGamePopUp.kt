package com.example.gomoku.ui.common.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveGamePopUp(
    nameGame: String,
    descriptionGame: String,
    setNameGame: (String) -> Unit,
    setDescriptionGame: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onBackLobbyClick: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = { },
        title = {
            Column {
                TextField(
                    value = nameGame,
                    onValueChange = setNameGame,
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = descriptionGame,
                    onValueChange = setDescriptionGame,
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveClicked()
                    onBackLobbyClick()
                }
            ) {
                Text("Save Game")
            }
        },
    )
}
