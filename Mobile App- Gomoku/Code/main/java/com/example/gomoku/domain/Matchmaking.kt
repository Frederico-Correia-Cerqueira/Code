package com.example.gomoku.domain

enum class Status { WAITING, READY_TO_CREATE }

data class Matchmaking(
    val playerID: Int?,
    val gameType: String?,
    val openingRules: String?
)

data class MatchmakingDto(
    val status: Status,
    val gameID: Int?
)

data class PlayerInfo(
    val playerID: Int,
    val name: String
)