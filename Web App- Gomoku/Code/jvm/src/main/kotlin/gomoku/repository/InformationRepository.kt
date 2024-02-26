package gomoku.repository

import gomoku.domainEntities.Stats

interface InformationRepository {
    fun addToStats(playerID: Int)
    fun getStats(): List<Stats>
    fun updateStats(pid: Int, stats: Stats)
    fun getPlayerStats(playerID: Int): Stats
}