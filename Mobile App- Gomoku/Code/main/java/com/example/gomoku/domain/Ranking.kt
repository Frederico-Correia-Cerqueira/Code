package com.example.gomoku.domain

data class Stats(
    val player: String,
    val elo: Double,
    val victories: Int,
    val defeats: Int,
    val totalGames: Int,
    val winRate: Int,
    val rank: Rank
)

data class Ranking(val stats: List<Stats>)
enum class Rank(val goal: Int) {
    UNRANKED(50),
    NOOB(100),
    BEGINNER(300),
    INTERMEDIATE(500),
    EXPERT(1000),
    PRO(5000)
}