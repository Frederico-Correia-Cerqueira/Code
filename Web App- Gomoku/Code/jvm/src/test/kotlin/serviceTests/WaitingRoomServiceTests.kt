package serviceTests


import gomoku.domainEntities.Either
import gomoku.domainEntities.Matchmaking
import gomoku.domainEntities.MatchmakingResult
import gomoku.domainEntities.OpeningRules
import gomoku.domainEntities.PlayerState
import gomoku.domainEntities.Status
import gomoku.domainEntities.Token
import gomoku.domainEntities.User
import gomoku.domainEntities.Variants
import gomoku.http.models.PlayerInputModel
import gomoku.repository.jdbi.JdbiInformationRepository
import gomoku.repository.jdbi.JdbiPlayersRepository
import gomoku.repository.jdbi.JdbiTransactionManager
import gomoku.repository.jdbi.mappers.BoardMapper
import gomoku.repository.jdbi.mappers.GameMapper
import gomoku.repository.jdbi.mappers.PlayerMapper
import gomoku.repository.jdbi.mappers.StatsMapper
import gomoku.repository.jdbi.mappers.WaiterMapper
import gomoku.service.WaitingRoomService
import gomoku.utils.Error
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.postgresql.ds.PGSimpleDataSource
import java.util.UUID

class WaitingRoomServiceTests {

    private lateinit var waitingRoomService: WaitingRoomService
    private lateinit var playersRepository: JdbiPlayersRepository
    private lateinit var informationRepository: JdbiInformationRepository
    private lateinit var transactionManager: JdbiTransactionManager
    @BeforeEach
    fun setup() {
        transactionManager = createTransactionManager()
        waitingRoomService = createWaitingRoomService(transactionManager = transactionManager)
    }

    @Test
    fun `can add one player to waiting room`() {
        val playerInputModel = PlayerInputModel("Test", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            informationRepository = createInformationRepository(handle)
            val user: User = playersRepository.createPlayer(playerInputModel.name, playerInputModel.password, PlayerState.IDLE)
            informationRepository.addToStats(user.id)
            playersRepository.createToken(Token(UUID.randomUUID(), user.id), 1)
            when(val res: MatchmakingResult = waitingRoomService.join(user.id, OpeningRules.NORMAL, Variants.NORMAL)) {
                is Either.Right -> assertEquals(Matchmaking(Status.WAITING, null), res.value)
                is Either.Left -> fail("Unexpected $res")
            }

            handle.rollback()
        }

    }

    @Test
    fun `can add two different player's to waiting room with same variant and rule and create a game`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            informationRepository = createInformationRepository(handle)
            val user1: User = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2: User = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            informationRepository.addToStats(user1.id)
            informationRepository.addToStats(user2.id)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)
            val res1: MatchmakingResult = waitingRoomService.join(user1.id, OpeningRules.NORMAL, Variants.NORMAL)
            val res2: MatchmakingResult = waitingRoomService.join(user2.id, OpeningRules.NORMAL, Variants.NORMAL)
            when(res1) {
                is Either.Right -> assertEquals(Matchmaking(Status.WAITING, null), res1.value)
                is Either.Left -> fail("Unexpected $res1")
            }
            when(res2) {
                is Either.Right -> assertEquals(Status.READY_TO_CREATE, res2.value.status)
                is Either.Left -> fail("Unexpected $res2")
            }

            handle.rollback()
        }
    }

    @Test
    fun `can add tree different player's to waiting room with different variant and rule`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        val playerInputModel3 = PlayerInputModel("Test3", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            informationRepository = createInformationRepository(handle)

            val user1: User = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2: User = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            val user3: User = playersRepository.createPlayer(playerInputModel3.name, playerInputModel3.password, PlayerState.IDLE)
            informationRepository.addToStats(user1.id)
            informationRepository.addToStats(user2.id)
            informationRepository.addToStats(user3.id)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user3.id), 1)
            val res1: MatchmakingResult = waitingRoomService.join(user1.id, OpeningRules.NORMAL, Variants.NORMAL)
            val res2: MatchmakingResult = waitingRoomService.join(user2.id, OpeningRules.NORMAL, Variants.SWAP_FIRST_MOVE)
            val res3: MatchmakingResult = waitingRoomService.join(user3.id, OpeningRules.SWAP, Variants.NORMAL)
            when(res1) {
                is Either.Right -> assertEquals(Matchmaking(Status.WAITING, null), res1.value)
                is Either.Left -> fail("Unexpected $res1")
            }

            when(res2) {
                is Either.Right -> assertEquals(Matchmaking(Status.WAITING, null), res2.value)
                is Either.Left -> fail("Unexpected $res2")
            }

            when(res3) {
                is Either.Right -> assertEquals(Matchmaking(Status.WAITING, null), res3.value)
                is Either.Left -> fail("Unexpected $res3")
            }

            handle.rollback()
        }
    }


    @Test
    fun `can´t join waiting room without login`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")

        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            informationRepository = createInformationRepository(handle)

            val user1: User = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            informationRepository.addToStats(user1.id)
            val res: MatchmakingResult = waitingRoomService.join(user1.id, OpeningRules.NORMAL, Variants.NORMAL)

            when(res) {
                is Either.Right -> fail("Unexpected $res")
                is Either.Left -> assertEquals(Error.TokenNotFound, res.value)
            }

            handle.rollback()
        }
    }
    companion object {
        private fun createTransactionManager() = JdbiTransactionManager(jdbi)
        private fun createWaitingRoomService(transactionManager: JdbiTransactionManager) =
            WaitingRoomService(transactionManager = transactionManager)
        private fun createPlayersRepository(handle: Handle) = JdbiPlayersRepository(handle = handle)
        private fun createInformationRepository(handle: Handle) = JdbiInformationRepository(handle = handle)

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("jdbc:postgresql://localhost/DAW_TESTS?user=postgres&password=PedroAdmin")
            }
        ).configureWithAppRequirements()

        private fun Jdbi.configureWithAppRequirements(): Jdbi {
            installPlugin(KotlinPlugin())
            installPlugin(PostgresPlugin())

            registerRowMapper(PlayerMapper())
            registerRowMapper(BoardMapper())
            registerRowMapper(GameMapper())
            registerRowMapper(WaiterMapper())
            registerRowMapper(StatsMapper())

            return this
        }
    }
}