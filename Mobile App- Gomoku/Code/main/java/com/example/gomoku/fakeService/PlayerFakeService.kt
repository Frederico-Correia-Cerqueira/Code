package com.example.gomoku.fakeService

import com.example.gomoku.PlayerService
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Ranking
import com.example.gomoku.domain.Stats
import com.example.gomoku.domain.UserDto
import java.util.UUID

class PlayerFakeService : PlayerService {

    override suspend fun fetchGetUser(id: Int) =
        UserDto(1, "Alice", "123", "IDLE")

    override suspend fun fetchPlayerStats(id: Int) =
        Stats(
            "Alice",
            100.0,
            1,
            1,
            2,
            50,
            Rank.UNRANKED
        )

    override suspend fun fetchCreateUser(name: String, password: String) =
        CreateUserDto(UUID.randomUUID(), 1)

    val stats = listOf(
        Stats("Fred", 1000.0, 1, 1, 2, 50, Rank.UNRANKED),
        Stats("Ricardo", 900.0, 20, 0, 20, 50, Rank.NOOB),
        Stats("Pedro", 500.0, 10, 1, 2, 50, Rank.PRO),
        Stats("Catarina", 300.0, 1, 10, 2, 50, Rank.EXPERT),
        Stats("Bob", 200.0, 10, 10, 2, 100, Rank.UNRANKED),
        Stats("Alice", 100.0, 1, 1, 2, 0, Rank.PRO),
        Stats("Carol", 10.0, 1, 1, 2, 0, Rank.EXPERT),
        Stats("Alberto", 0.0, 1, 1, 2, 50, Rank.NOOB),
        Stats("Zé", 100.0, 10, 10, 2, 100, Rank.NOOB),
        Stats("Maria", 100.0, 1, 1, 2, 75, Rank.PRO),
        Stats("Joao", 10.0, 1, 1, 2, 50, Rank.EXPERT),
        Stats("Joaquim", 5.0, 1, 1, 2, 50, Rank.UNRANKED),
        Stats("Joaquina", 1.0, 1, 1, 2, 50, Rank.UNRANKED),
        Stats("Antonio", 100.0, 1, 1, 2, 50, Rank.UNRANKED),
        Stats("Antonia", 3000.0, 1, 1, 2, 50, Rank.UNRANKED)
    )

    override suspend fun fetchStats(): Ranking {
        return Ranking(stats)
    }
}