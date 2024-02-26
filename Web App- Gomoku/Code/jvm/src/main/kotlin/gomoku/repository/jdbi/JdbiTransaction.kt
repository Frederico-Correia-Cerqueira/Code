package gomoku.repository.jdbi

import gomoku.repository.*
import org.jdbi.v3.core.Handle

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val playersRepository: PlayersRepository = JdbiPlayersRepository(handle)
    override val gamesRepository: GamesRepository = JdbiGamesRepository(handle)
    override val waitingRoomRepository: WaitingRoomRepository = JdbiWaitingRoomRepository(handle)
    override val informationRepository: InformationRepository = JdbiInformationRepository(handle)
    override fun rollback() {
        handle.rollback()
    }
}