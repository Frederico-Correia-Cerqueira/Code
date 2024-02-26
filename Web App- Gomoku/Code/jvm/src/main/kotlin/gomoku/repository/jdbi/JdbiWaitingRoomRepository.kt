package gomoku.repository.jdbi

import gomoku.domainEntities.*
import gomoku.repository.WaitingRoomRepository
import org.jdbi.v3.core.Handle

class JdbiWaitingRoomRepository(private val handle: Handle) : WaitingRoomRepository {

    /**
     * Adds a player to the waiting room.
     * @param playerID The ID of the player to add to the waiting room.
     */
    override fun addPlayerToWaitingRoom(playerID: Int, variants: Variants, openingRules: OpeningRules, rank: Rank) {
        handle.createUpdate(
            "insert into waitingRoom(playerID, variants, openingRules, rank) values (:playerID, :variants, :openingRules, :rank)"
        )
            .bind("playerID", playerID)
            .bind("variants", variants)
            .bind("openingRules", openingRules)
            .bind("rank", rank)
            .execute()
    }

    /**
     * Gets an available player from the waiting room.
     * @return A `Waiter` object representing the available player, or `null` if there is no available player.
     */
    override fun getAvailablePlayer(variants: Variants, openingRules: OpeningRules, rank: Rank): Waiter? =
        handle.createQuery("select id, playerID from waitingRoom where variants = :variants AND openingRules = :openingRules AND rank = :rank ")
            .bind("variants", variants)
            .bind("openingRules", openingRules)
            .bind("rank", rank)
            .mapTo(Waiter::class.java)
            .firstOrNull()

    /**
     * Removes a player from the waiting room.
     * @param playerID ID of the player to the remove
     */
    override fun removePlayerFromWaitingRoom(playerID: Int) {
        handle.createUpdate(
            "delete from waitingRoom where playerID = :playerID"
        )
            .bind("playerID", playerID)
            .execute()
    }
}