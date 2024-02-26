package com.example.gomoku.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gomoku.ui.common.Form

@Composable
fun LoginScreen(
    onFetch: () -> Unit = { },
    code: Int,
    onCreateUser: () -> Unit = {},
    name: String,
    password: String,
    enableToLogin: Boolean,
    onNameChange: (String) -> Unit = { _ -> },
    onPasswordChange: (String) -> Unit = { _ -> },
    onNavigateError: () -> Unit = {}
) {
    if (code == 500) {
        onNavigateError()
    }
    val message = if (code == 400) "Username or password incorrect" else ""
    Form(
        name = name,
        password = password,
        message = message,
        enable = enableToLogin,
        navigateText = "Create User",
        action = "Login",
        onFetch = onFetch,
        onNavigate = onCreateUser,
        onNameChange = onNameChange,
        onPasswordChange = onPasswordChange
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(name = "", code = 400, password = "", enableToLogin = true)
}

