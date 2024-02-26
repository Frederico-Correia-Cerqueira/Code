package com.example.gomoku.http

import android.util.Log
import com.example.gomoku.FetchException
import com.example.gomoku.TAG
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private fun buildLazyRequest(uri: String, requestBody: RequestBody?, path: String): Lazy<Request> {
    return lazy {
        if (requestBody == null)
            Request.Builder()
                .url("$uri/$path")
                .addHeader("accept", "application/json")
                .build()
        else {
            Request.Builder()
                .url("$uri/$path")
                .addHeader("accept", "application/json")
                .post(requestBody)
                .build()
        }
    }
}

suspend fun <T> httpHandler(
    uri: String,
    client: OkHttpClient,
    gson: Gson,
    requestBody: RequestBody?,
    path: String,
    clazz: Class<T>
): T {
    return suspendCoroutine {
        val lazyRequest = buildLazyRequest(uri, requestBody, path)
        val request by lazyRequest
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(FetchException("Failed to fetch /$path", 500, e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (!response.isSuccessful || body == null)
                    it.resumeWithException(
                        FetchException("Failed to fetch /$path", response.code)
                    )
                else {
                    try {
                        val siren = gson.fromJson(body.string(), SirenModel::class.java)
                        val json = gson.toJson(siren.properties)
                        Log.v(TAG, "SIREN= ${siren.properties.toString()}")
                        val dto = gson.fromJson(json, clazz)
                        Log.v(TAG, "DTO= $dto")
                        it.resumeWith(Result.success(dto))
                    } catch (e: Exception) {
                        Log.e(TAG, "e= $e")
                    }
                }
            }
        })
    }
}