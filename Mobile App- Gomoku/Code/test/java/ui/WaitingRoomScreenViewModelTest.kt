package ui

import com.example.gomoku.domain.IOState
import com.example.gomoku.MatchmakingService
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.Idle
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.waitingRoom.WaitingRoomScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import utils.MockMainDispatcherRule
import utils.SuspendingGate

@OptIn(ExperimentalCoroutinesApi::class)
class WaitingRoomScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val matchmakingService = mockk<MatchmakingService>()
    private val userDataStore = mockk<UserDataStore> {
        coEvery { updateUserInfo(any()) } returns Unit
    }
    private val sut = WaitingRoomScreenViewModel(matchmakingService, userDataStore)


    @Test
    fun initially_the_gameID_state_flow_is_idle() = runTest {
        // Arrange
        // Act
        val gate = SuspendingGate()
        var collectedState: IOState<GameIDDto>? = null
        val collectJob = launch {
            sut.gameID.collect {
                collectedState = it
                gate.open()
            }
        }

        // Wait for the flow to emit the first value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Assert
        Assert.assertTrue("Expected Idle but got $collectedState instead", collectedState is Idle)
    }
}
