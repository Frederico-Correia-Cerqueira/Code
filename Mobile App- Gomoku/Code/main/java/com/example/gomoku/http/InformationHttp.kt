package com.example.gomoku.http

import com.example.gomoku.InformationService
import com.example.gomoku.domain.Information
import com.google.gson.Gson
import okhttp3.OkHttpClient

class InformationHttp(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val uri: String
) : InformationService {

    override suspend fun fetchCredits() =
        httpHandler(
            uri,
            client,
            gson,
            null,
            "credits",
            Information::class.java
        )
}