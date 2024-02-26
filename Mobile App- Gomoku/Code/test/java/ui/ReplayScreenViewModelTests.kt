package ui

import com.example.gomoku.GameService
import com.example.gomoku.PlayerService
import com.example.gomoku.domain.Board
import com.example.gomoku.domain.BoardTypes
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loading
import com.example.gomoku.domain.OpeningRules
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.Variants
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.replay.ReplayScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import utils.MockMainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class ReplayScreenViewModelTests {
    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val gameService = mockk<GameService> {
        coEvery { fetchGetGame(any()) } coAnswers {
            GameDto(
                1,
                0,
                0,
                Board(emptyMap(), 0, BoardTypes.BOARD_RUN),
                Variants.NORMAL,
                OpeningRules.NORMAL,
                false
            )
        }
    }
    private val playerService = mockk<PlayerService> {
        coEvery { fetchGetUser(any()) } coAnswers {
            UserDto(0, "testUsername", "testPassword", "testState")
        }
    }

    private val userDataStore = mockk<UserDataStore> {
        coEvery { updateUserInfo(any()) } coAnswers {

        }
        coEvery { getUserInfo() } coAnswers {
            DataStoreDto("testToken", 0, "testUsername")
        }
    }
    private val sut = ReplayScreenViewModel(gameService, playerService, userDataStore)

    @Test
    fun `initially the game mutableState state is idle`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.game} instead", sut.game is Idle)
    }

    @Test
    fun `initially the opponent mutableState state is idle`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.opponent} instead", sut.opponent is Idle)
    }

    @Test
    fun `fetchGame emits to the game mutableState the loaded state`() = runTest {
        // Arrange
        // Act
        var state: IOState<GameDto?>? = null
        sut.fetchGame(0)
        if (sut.game is Loading) {
            state = sut.game
        }

        Assert.assertNotNull(
            "Expected Loaded but got $state instead",
            state
        )
    }

    @Test
    fun `fetchGame emits to the game mutableState the loading state`() = runTest {
        // Arrange
        // Act
        sut.fetchGame(1)
        // Assert
        val loading = sut.game as? Loading
        Assert.assertNotNull(
            "Expected Loaded but got ${sut.game} instead",
            loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `fetchGame throws if state is not idle`() = runTest {
        // Arrange
        sut.fetchGame(0)
        // Act
        sut.fetchGame(0)
    }

    @Test
    fun `initially the current mutableState is null`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertNull(
            "initially current mutableState should be null but is${sut.current}!",
            sut.current
        )
    }

    @Test
    fun `initially the playIdx mutableState is zero`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue(
            "initially playIdx mutableState should be zero but is${sut.playIdx}!",
            sut.playIdx == 0
        )
    }

    @Test
    fun `incrementIdx and decrementIdx changes the playIdx mutableState value by one`() = runTest {
        // Arrange
        val playIdxInitial = sut.playIdx
        // Act
        sut.incrementIdx()
        val playIdxIncremented = sut.playIdx
        sut.decrementIdx()
        val playIdxDecremented = sut.playIdx
        // Assert
        Assert.assertTrue(
            "incrementIdx and decrementIdx don't change the playIdx mutableState value by one",
            playIdxIncremented == playIdxInitial + 1
        )
        Assert.assertTrue(
            "incrementIdx and decrementIdx don't change the playIdx mutableState value by one",
            playIdxDecremented == playIdxIncremented - 1
        )
        Assert.assertTrue(
            "incrementIdx and decrementIdx don't change the playIdx mutableState value by one",
            playIdxDecremented == playIdxInitial
        )
    }


}