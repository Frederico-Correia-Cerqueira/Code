package com.example.gomoku.ui.createUser


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gomoku.ui.common.Form

@Composable
fun CreateUserScreen(
    onFetch: () -> Unit = { },
    code: Int,
    onCreateUser: () -> Unit = {},
    name: String,
    password: String,
    enableToCreateUser: Boolean,
    onNameChange: (String) -> Unit = { _ -> },
    onPasswordChange: (String) -> Unit = { _ -> },
    onNavigateError: () -> Unit = {}
) {
    if (code == 500) {
        onNavigateError()
    }
    val message = if (code == 409) "There is already a user with that name" else ""
    Form(
        name = name,
        password = password,
        message = message,
        enable = enableToCreateUser,
        navigateText = "Login",
        action = "Create User",
        onFetch = onFetch,
        onNavigate = onCreateUser,
        onNameChange = onNameChange,
        onPasswordChange = onPasswordChange
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    CreateUserScreen(name = "", code = 409, password = "", enableToCreateUser = true)
}