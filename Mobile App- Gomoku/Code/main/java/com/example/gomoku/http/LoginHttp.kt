package com.example.gomoku.http

import com.example.gomoku.LoginService
import com.example.gomoku.domain.LoginDto
import com.example.gomoku.domain.LogoutDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class LoginHttp(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val uri: String
) : LoginService {

    private data class LoginForm(val name: String, val password: String)
    private data class LogoutForm(val token: String)


    private fun requestBodyLogin(name: String, password: String): RequestBody {
        return gson.toJson(LoginForm(name, password))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun requestBodyLogout(token: String): RequestBody {
        return gson.toJson(LogoutForm(token))
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    override suspend fun fetchLogin(name: String, password: String) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodyLogin(name, password),
            "login",
            LoginDto::class.java
        )

    override suspend fun fetchLogout(token: String) =
        httpHandler(
            uri,
            client,
            gson,
            requestBodyLogout(token),
            "logout",
            LogoutDto::class.java
        )
}