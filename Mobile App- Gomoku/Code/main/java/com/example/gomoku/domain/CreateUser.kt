package com.example.gomoku.domain

import java.util.UUID

data class CreateUserDto(
    val token: UUID,
    val userID: Int
)