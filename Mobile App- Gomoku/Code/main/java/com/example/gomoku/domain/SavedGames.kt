package com.example.gomoku.domain

data class SavedGames(val games: List<SavedGame>)

data class SavedGame(
    val id: Int,
    val game: Int,
    val name: String,
    val description: String
)