package serviceTests

import gomoku.domainEntities.Either
import gomoku.http.models.PlayerInputModel
import gomoku.repository.jdbi.JdbiTransactionManager
import gomoku.repository.jdbi.mappers.BoardMapper
import gomoku.repository.jdbi.mappers.GameMapper
import gomoku.repository.jdbi.mappers.PlayerMapper
import gomoku.repository.jdbi.mappers.StatsMapper
import gomoku.repository.jdbi.mappers.WaiterMapper
import gomoku.service.PlayersService
import gomoku.utils.Error
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource

class PlayersServicesTests {

    private lateinit var playersService: PlayersService
    private lateinit var transactionManager: JdbiTransactionManager
    @BeforeEach
    fun setup() {
        transactionManager = createTransactionManager()
        playersService = createPlayersService(transactionManager = transactionManager)
    }

    @Test
    fun `can create one player`() {
        val playerInputModel = PlayerInputModel("Test", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            when(val res = playersService.createPlayer(playerInputModel.name, playerInputModel.password)) {
                is Either.Right -> assertTrue(res.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res")
            }
            handle.rollback()
        }
    }

    @Test
    fun `can't add two users with the same username`() {
        val playerInputModel = PlayerInputModel("Test", "123123")
        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            when(val res1 = playersService.createPlayer(playerInputModel.name, playerInputModel.password)) {
                is Either.Right -> assertTrue(res1.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res1")
            }

            when(val res2 = playersService.createPlayer(playerInputModel.name, playerInputModel.password)) {
                is Either.Right -> Assertions.fail("Unexpected $res2")
                is Either.Left -> assertEquals(Error.PlayerAlreadyExists, res2.value)
            }

            handle.rollback()
        }
    }

    @Test
    fun `can add two users with different usernames`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")
        val playerInputModel2 = PlayerInputModel("Test2", "123123")

        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            val res1 = playersService.createPlayer(playerInputModel1.name, playerInputModel1.password)
            when(res1) {
                is Either.Right -> assertTrue(res1.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res1")
            }

            val res2 = playersService.createPlayer(playerInputModel2.name, playerInputModel2.password)
            when(res2) {
                is Either.Right -> assertTrue(res2.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res2")
            }

            if(res1 is Either.Right && res2 is Either.Right) {
                assertTrue(res1.value.userID < res2.value.userID)
            }
            handle.rollback()
        }
    }

    @Test
    fun `login non created user`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")

        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            when(val res = playersService.createToken(playerInputModel1.name, playerInputModel1.password)) {
                is Either.Right -> Assertions.fail("Unexpected $res")
                is Either.Left -> assertEquals(Error.UserOrPasswordInvalid, res.value)
            }

            handle.rollback()
        }
    }

    @Test
    fun `login created user`() {
        val playerInputModel1 = PlayerInputModel("Test1", "123123")

        jdbi.useHandle<Exception> { handle ->
            handle.begin()
            when(val res1 = playersService.createPlayer(playerInputModel1.name, playerInputModel1.password)) {
                is Either.Right -> assertTrue(res1.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res1")
            }

            when(val res2 = playersService.createToken(playerInputModel1.name, playerInputModel1.password)) {
                is Either.Right -> assertTrue(res2.value.userID > 0)
                is Either.Left -> Assertions.fail("Unexpected $res2")
            }

            handle.rollback()
        }
    }

    companion object {
        private fun createTransactionManager() = JdbiTransactionManager(jdbi)
        private fun createPlayersService(transactionManager: JdbiTransactionManager): PlayersService =
            PlayersService(transactionManager = transactionManager)

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