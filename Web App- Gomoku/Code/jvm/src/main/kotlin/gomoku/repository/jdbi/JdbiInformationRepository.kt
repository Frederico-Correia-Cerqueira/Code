package gomoku.repository.jdbi

import gomoku.domainEntities.Rank
import gomoku.domainEntities.Stats
import gomoku.repository.InformationRepository
import org.jdbi.v3.core.Handle

class JdbiInformationRepository(private val handle: Handle) : InformationRepository {

    /**
     * Adds a player to the stats table.
     * @param playerID The player to add.
     */
    override fun addToStats(playerID: Int) {
        handle.createUpdate(
            "insert into stats(playerID, elo, victories, defeats, totalGames, winRate, rank) values " +
                    "(:playerID, :elo, :victories, :defeats, :totalGames, :winRate, :rank)"
        )
            .bind("playerID", playerID)
            .bind("elo", 0.0)
            .bind("victories", 0)
            .bind("defeats", 0)
            .bind("totalGames", 0)
            .bind("winRate", 0)
            .bind("rank", Rank.UNRANKED)
            .execute()
    }


    /**
     * Gets the stats for all player's
     * @return a `Stats` object representing the statistics for all player's
     */
    override fun getStats(): List<Stats> =
        handle.createQuery("SELECT p.username as player, s.elo, s.victories, s.defeats, s.totalGames, s.winRate, s.rank FROM STATS s JOIN PLAYER p ON s.playerID = p.id")
            .mapTo(Stats::class.java)
            .toList()


    /**
     * Updates a player's stats
     * @param stats the player's updated stats
     */
    override fun updateStats(pid: Int, stats: Stats) {
        handle.createUpdate("update stats set elo=:elo, victories=:victories, defeats=:defeats, totalGames=:totalGames, winRate=:winRate, rank=:rank where playerID=:playerID")
            .bind("playerID", pid)
            .bind("elo", stats.elo)
            .bind("victories", stats.victories)
            .bind("defeats", stats.defeats)
            .bind("totalGames", stats.totalGames)
            .bind("winRate", stats.winRate)
            .bind("rank", stats.rank)
            .execute()
    }

    /**
     * Gets the stats of a specific player
     * @param playerID the player's ID
     * @return a `Stats` object representing the statistics for the given player
     */
    override fun getPlayerStats(playerID: Int): Stats =
        handle.createQuery("SELECT p.username as player, s.elo, s.victories, s.defeats, s.totalGames, s.winRate, s.rank FROM STATS s JOIN PLAYER p ON s.playerID = p.id WHERE s.playerID = :playerID")
            .bind("playerID", playerID)
            .mapTo(Stats::class.java)
            .first()
}