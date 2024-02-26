package ui

import com.example.gomoku.PlayerService
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loading
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.ranking.RankingScreenViewModel
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
class RankingScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val playerService = mockk<PlayerService> {
        coEvery { fetchStats().stats } coAnswers {
            emptyList()
        }
    }

    private val userDataStore = mockk<UserDataStore> {
        coEvery { updateUserInfo(any()) } coAnswers {

        }
        coEvery { getUserInfo() } coAnswers {
            DataStoreDto("testToken", 0, "testUsername")
        }
    }
    private val sut = RankingScreenViewModel(playerService, userDataStore)


    @Test
    fun `initially the stats mutableState state is idle`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.stats} instead", sut.stats is Idle)
    }

    @Test
    fun `fetchStats emits to the stats mutableState the loaded state`() = runTest {
        // Arrange
        // Act
        var state: IOState<List<Stats>?>? = null
        sut.fetchStats()
        if (sut.stats is Loading) {
            state = sut.stats
        }

        Assert.assertNotNull(
            "Expected Loaded but got $state instead",
            state
        )
    }

    @Test
    fun `fetchStats emits to the stats mutableState the loading state`() = runTest {
        // Arrange
        // Act
        sut.fetchStats()
        // Assert
        val loading = sut.stats as? Loading
        Assert.assertNotNull(
            "Expected Loaded but got ${sut.stats} instead",
            loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `fetchStats throws if state is not idle`() = runTest {
        // Arrange
        sut.fetchStats()
        // Act
        sut.fetchStats()
    }

    @Test
    fun `initially the ranking mutableState is null`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertNull(
            "initially ranking mutableState should be null but is${sut.ranking}!",
            sut.ranking
        )
    }

    @Test
    fun `initially the playerName and player mutableStates values are emptyString`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue(
            "initially playerName mutableState should be emptyString but is${sut.playerName}!",
            sut.playerName == ""
        )
        Assert.assertTrue(
            "initially player mutableState should be emptyString but is${sut.player}!",
            sut.player == ""
        )
    }

    @Test
    fun `setRank changes the ranking mutableState value`() = runTest {
        // Arrange
        val ranking = sut.ranking
        // Act
        sut.setRank(Rank.NOOB)
        // Assert
        Assert.assertTrue(
            "Ranking mutableState value didn't change on setRank called",
            ranking != sut.ranking
        )
    }

    @Test
    fun `setPlayers changes the player mutableState value`() = runTest {
        // Arrange
        val player = sut.player
        // Act
        sut.setPlayers("testPlayer")
        // Assert
        Assert.assertTrue(
            "Player mutableState value didn't change on setPlayers called",
            player != sut.player
        )
    }
}