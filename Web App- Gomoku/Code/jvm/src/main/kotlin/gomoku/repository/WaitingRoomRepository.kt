package gomoku.repository

import gomoku.domainEntities.*

interface WaitingRoomRepository {
    fun addPlayerToWaitingRoom(playerID: Int, variants: Variants, openingRules: OpeningRules, rank: Rank)
    fun getAvailablePlayer(variants: Variants, openingRules: OpeningRules, rank: Rank): Waiter?
    fun removePlayerFromWaitingRoom(playerID: Int)
}