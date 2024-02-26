package com.example.gomoku.domain

data class UserDto(
    val id: Int,
    val username: String,
    val password: String,
    val state: String
)
