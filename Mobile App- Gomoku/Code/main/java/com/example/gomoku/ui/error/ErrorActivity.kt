package com.example.gomoku.ui.error

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.example.gomoku.TAG
import com.example.gomoku.ui.home.HomeActivity


class ErrorActivity : ComponentActivity() {
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.v(TAG, "LoginActivity.onBackPressed() called")
        }
    }

    companion object {
        fun navigateTo(origin: ComponentActivity) {
            val intent = Intent(origin, ErrorActivity::class.java)
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        Log.e(TAG, "ErrorActivity.onCreate() called")
        setContent {
            ErrorScreen { HomeActivity.navigateTo(this) }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "ErrorActivity.onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "ErrorActivity.onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "ErrorActivity.onDestroy() called")
    }
}