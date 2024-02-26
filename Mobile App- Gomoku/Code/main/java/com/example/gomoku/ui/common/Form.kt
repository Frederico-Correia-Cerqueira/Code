package com.example.gomoku.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.example.gomoku.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form(
    name: String,
    password: String,
    message: String,
    enable: Boolean,
    action: String,
    navigateText: String,
    onFetch: () -> Unit,
    onNavigate: () -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Username") }
        )
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        if (message.isNotEmpty()) {

            Text(text = message, color = Color.Red)
        }
        Button(
            onClick = { onFetch() },
            enabled = enable
        ) {
            Text(action)
        }
        Text(text = buildAnnotatedString {
            append(navigateText)
            addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                ),
                start = 0,
                end = navigateText.length
            )
        }, modifier = Modifier.clickable(onClick = onNavigate))
    }
}