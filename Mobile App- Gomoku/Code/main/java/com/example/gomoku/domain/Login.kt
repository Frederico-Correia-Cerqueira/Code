package com.example.gomoku.domain

data class LoginDto(val token: String, val userID: Int)
data class LogoutDto(val success: Boolean)