package serviceTests

import gomoku.domainEntities.BoardTypes
import gomoku.domainEntities.Either
import gomoku.domainEntities.OpeningRules
import gomoku.domainEntities.Pieces
import gomoku.domainEntities.PlayerState
import gomoku.domainEntities.Square
import gomoku.domainEntities.SquareDetails
import gomoku.domainEntities.Token
import gomoku.domainEntities.Variants
import gomoku.domainEntities.initialBoard
import gomoku.domainEntities.newTurn
import gomoku.http.models.PlayerInputModel
import gomoku.repository.jdbi.JdbiGamesRepository
import gomoku.repository.jdbi.JdbiInformationRepository
import gomoku.repository.jdbi.JdbiPlayersRepository
import gomoku.repository.jdbi.JdbiTransactionManager
import gomoku.repository.jdbi.mappers.BoardMapper
import gomoku.repository.jdbi.mappers.GameMapper
import gomoku.repository.jdbi.mappers.PlayerMapper
import gomoku.repository.jdbi.mappers.StatsMapper
import gomoku.repository.jdbi.mappers.WaiterMapper
import gomoku.service.GamesService
import gomoku.utils.Error
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.util.UUID

class GamesServicesTests {

    private lateinit var gamesService: GamesService
    private lateinit var transactionManager: JdbiTransactionManager
    private lateinit var playersRepository: JdbiPlayersRepository
    private lateinit var informationRepository: JdbiInformationRepository
    private lateinit var gamesRepository: JdbiGamesRepository
    @BeforeEach
    fun setup() {
        transactionManager = createTransactionManager()
        gamesService = createGamesService(transactionManager = transactionManager)
    }

    @Test
    fun `can't get game with invalid id`() {
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            when(val res = gamesService.getGame(1)) {
                is Either.Right -> Assertions.fail("Unexpected $res")
                is Either.Left -> assertEquals(Error.GameNotFound, res.value)
            }
            handle.rollback()
        }
    }

    @Test
    fun `can get game with valid id`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.NORMAL, OpeningRules.NORMAL)
            when(val res = gamesService.getGame(gameID)) {
                is Either.Right -> assertTrue(res.value.id > 0)
                is Either.Left -> Assertions.fail("Unexpected $res")
            }

            handle.rollback()
        }
    }

    @Test
    fun `same player cant play two times in a row`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.NORMAL, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                when(val res = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> assertTrue(player2Turn == res.value.board.turn)
                    is Either.Left -> Assertions.fail("Unexpected $res")
                }
                when(val res1 = gamesService.play(gameID, player1Turn, 1, 0)) {
                    is Either.Right -> Assertions.fail("Unexpected $res1")
                    is Either.Left -> assertEquals(Error.NotYourTurn, res1.value)
                }

            }

            handle.rollback()
        }
    }

    @Test
    fun `can't play at taken position`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.NORMAL, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                when(val res = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> assertTrue(player2Turn == res.value.board.turn)
                    is Either.Left -> Assertions.fail("Unexpected $res")
                }
                when(val res1 = gamesService.play(gameID, player2Turn, 0, 0)) {
                    is Either.Right -> Assertions.fail("Unexpected $res1")
                    is Either.Left -> assertEquals(Error.PositionTaken, res1.value)
                }
                gamesService.play(gameID, player2Turn, 1, 0)

                when(val res2 = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> Assertions.fail("Unexpected $res2")
                    is Either.Left -> assertEquals(Error.PositionTaken, res2.value)
                }

            }

            handle.rollback()
        }
    }


    @Test
    fun `can play a NORMAL, NORMAL game and can't swapFirstMove`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.NORMAL, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                when(val res = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> assertTrue(player2Turn == res.value.board.turn)
                    is Either.Left -> Assertions.fail("Unexpected $res")
                }
                when(val res1 = gamesService.swapFirstMove(gameID, true, player2Turn)) {
                    is Either.Right -> Assertions.fail("Unexpected $res1")
                    is Either.Left -> assertEquals(Error.GameInfoNotDefined, res1.value)
                }

                when(val res2 = gamesService.play(gameID, player2Turn, 1, 0)) {
                    is Either.Right -> assertEquals(player1Turn, res2.value.board.turn)
                    is Either.Left -> Assertions.fail("Unexpected $res2")
                }

            }

            handle.rollback()
        }
    }

    @Test
    fun `can play a NORMAL, SWAP_FIRST_MOVE game`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                when(val res = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> assertTrue(player2Turn == res.value.board.turn)
                    is Either.Left -> Assertions.fail("Unexpected $res")
                }
                when(val info = gamesService.gameInfo(gameID)) {
                    is Either.Right -> assertTrue(info.value.info)
                    is Either.Left -> Assertions.fail("Unexpected $info")
                }
                when(val res1 = gamesService.swapFirstMove(gameID, true, player2Turn)) {
                    is Either.Right -> assertEquals(SquareDetails(player2Turn, Pieces.N), res1.value.board.moves[Square(0,0)])
                    is Either.Left -> Assertions.fail("Unexpected $res1")
                }
                when(val res2 = gamesService.play(gameID, player2Turn, 1, 0)) {
                    is Either.Right -> Assertions.fail("Unexpected $res2")
                    is Either.Left -> assertEquals(Error.NotYourTurn, res2.value)
                }

            }

            handle.rollback()
        }
    }

    @Test
    fun `can play a game and win`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            informationRepository = createInformationRepository(handle)
            informationRepository.addToStats(user1.id)
            informationRepository.addToStats(user2.id)
            playersRepository.createToken(Token(UUID.randomUUID(), user1.id), 1)
            playersRepository.createToken(Token(UUID.randomUUID(), user2.id), 1)


            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                gamesService.play(gameID, player1Turn, 0, 0)
                gamesService.play(gameID, player2Turn, 1, 1)
                gamesService.play(gameID, player1Turn, 0, 1)
                gamesService.play(gameID, player2Turn, 2, 2)
                gamesService.play(gameID, player1Turn, 0, 2)
                gamesService.play(gameID, player2Turn, 3, 3)
                gamesService.play(gameID, player1Turn, 0, 3)
                gamesService.play(gameID, player2Turn, 4, 4)
                when(val res = gamesService.play(gameID, player1Turn, 0, 4)) {
                    is Either.Right -> {
                        assertEquals(BoardTypes.BOARD_WIN, res.value.board.type)
                        assertEquals(player1Turn, res.value.board.turn)
                    }
                    is Either.Left -> Assertions.fail("Unexpected $res")
                }
            }

            handle.rollback()
        }
    }

    @Test
    fun `can't play without login`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            playersRepository = createPlayersRepository(handle)
            val user1 = playersRepository.createPlayer(playerInputModel1.name, playerInputModel1.password, PlayerState.IDLE)
            val user2 = playersRepository.createPlayer(playerInputModel2.name, playerInputModel2.password, PlayerState.IDLE)
            informationRepository = createInformationRepository(handle)
            informationRepository.addToStats(user1.id)
            informationRepository.addToStats(user2.id)

            gamesRepository = createGamesRepository(handle)
            val gameID = gamesRepository.insertGame(user1.id, user2.id, initialBoard(user1.id, user2.id), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
            val game = gamesRepository.getById(gameID)
            if (game != null) {
                val player1Turn = game.board.turn
                val player2Turn = game.newTurn()
                when(val res = gamesService.play(gameID, player1Turn, 0, 0)) {
                    is Either.Right -> Assertions.fail("Unexpected $res")
                    is Either.Left -> assertEquals(Error.TokenNotFound, res.value)
                }
            }

            handle.rollback()
        }
    }



    companion object {
        private fun createTransactionManager() = JdbiTransactionManager(jdbi)
        private fun createGamesService(transactionManager: JdbiTransactionManager): GamesService =
            GamesService(transactionManager = transactionManager)

        private fun createPlayersRepository(handle: Handle) = JdbiPlayersRepository(handle = handle)
        private fun createInformationRepository(handle: Handle) = JdbiInformationRepository(handle = handle)

        private fun createGamesRepository(handle: Handle) = JdbiGamesRepository(handle = handle)

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