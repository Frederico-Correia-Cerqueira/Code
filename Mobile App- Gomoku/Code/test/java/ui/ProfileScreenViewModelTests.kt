package ui

import com.example.gomoku.PlayerService
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loading
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.profile.ProfileScreenViewModel
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
class ProfileScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val playerService = mockk<PlayerService> {
        coEvery { fetchPlayerStats(any()) } coAnswers {
            Stats("testUsername", 0.0, 0, 0, 0, 0, Rank.NOOB)
        }
    }

    private val userDataStore = mockk<UserDataStore> {
        coEvery { getUserInfo() } coAnswers {
            DataStoreDto("testToken", 0, "testUsername")
        }
    }

    private val sut = ProfileScreenViewModel(playerService, userDataStore)

    @Test
    fun initially_the_player_mutableState_state_is_idle() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.player} instead", sut.player is Idle)
    }

    @Test
    fun fetchPlayer_emits_to_the_player_mutableState_the_loaded_state() = runTest {
        // Arrange
        // Act
        var state: IOState<Stats?>? = null
        sut.fetchPlayer()
        if (sut.player is Loading) {
            state = sut.player
        }
        Assert.assertNotNull(
            "Expected Loaded but got $state instead",
            state
        )
    }

    @Test
    fun fetchPlayer_emits_to_the_player_mutableState_the_loading_state() = runTest {
        // Arrange
        // Act
        sut.fetchPlayer()
        // Assert
        val loading = sut.player as? Loading
        Assert.assertNotNull(
            "Expected Loaded but got ${sut.player} instead",
            loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `fetchPlayer throws if state is not idle`() = runTest {
        // Arrange
        sut.fetchPlayer()
        // Act
        sut.fetchPlayer()
    }


}