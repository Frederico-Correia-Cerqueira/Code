package ui

import com.example.gomoku.PlayerService
import com.example.gomoku.domain.*
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.ui.createUser.CreateUserScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import utils.MockMainDispatcherRule
import utils.SuspendingGate
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserScreenViewModelTests {

    @get:Rule
    val mainDispatcherRule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val testCreateUserDto = CreateUserDto(UUID(0, 0), 10)
    private val playerService = mockk<PlayerService>()
    private val userDataStore = mockk<UserDataStore> {
        coEvery { updateUserInfo(any()) } returns Unit
    }

    private val sut = CreateUserScreenViewModel(playerService, userDataStore)

    @Test
    fun `initially the createUser state flow is idle`() = runTest {
        // Arrange
        // Act
        val gate = SuspendingGate()
        var collectedState: Idle? = null
        val collectJob = launch {
            sut.createUserDto.collect {
                if (it is Idle) {
                    collectedState = it
                    gate.open()
                }
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

    @Test
    fun `fetchCreateUser emits to the createUser state flow the loaded state`() = runTest {
        // Arrange

        coEvery { playerService.fetchCreateUser(any(), any()) } returns testCreateUserDto

        // Act
        val gate = SuspendingGate()
        var lastCollectedState: IOState<CreateUserDto?>? = null
        val collectJob = launch {
            sut.createUserDto.collect {
                if (it is Loaded) {
                    lastCollectedState = it
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchCreateUser()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Assert
        val loaded = lastCollectedState as? Loaded
        Assert.assertNotNull(
            "Expected Loaded but got $lastCollectedState instead",
            loaded
        )
    }

    @Test
    fun `fetchCreateUser emits to the createUser state flow the loading state`() = runTest {
        // Arrange
        coEvery { playerService.fetchCreateUser(any(), any()) } returns testCreateUserDto

        // Act
        val gate = SuspendingGate()
        var lastCollectedState: IOState<CreateUserDto?>? = null
        val collectJob = launch {
            sut.createUserDto.collect {
                if (it is Loading) {
                    lastCollectedState = it
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchCreateUser()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Assert
        val loading = lastCollectedState as? Loading
        assertNotNull("Expected Loading but got $lastCollectedState instead", loading)
    }

    @Test(expected = IllegalStateException::class)
    fun `fetchCreateUser throws if state is not idle`() = runTest {
        // Arrange
        sut.setName("username")
        sut.setPassword("password")
        sut.fetchCreateUser()

        // Act
        sut.fetchCreateUser()
    }

    @Test
    fun resetToIdle_sets_state_to_idle() = runTest {
        // Arrange
        coEvery { playerService.fetchCreateUser(any(), any()) } returns testCreateUserDto

        // Act
        val gate = SuspendingGate()
        val collectJob = launch {
            sut.createUserDto.collect {
                if (it is Loaded) {
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchCreateUser()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Act
        sut.resetToIdle()
        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }
        // Assert
        val currentState = sut.createUserDto.first()
        Assert.assertTrue("Expected Idle but got $currentState instead", currentState is Idle)
    }

    @Test(expected = IllegalStateException::class)
    fun resetToIdle_throws_if_state_is_not_loaded() = runTest {
        // Arrange
        sut.setName("username")
        sut.setPassword("password")
        sut.fetchCreateUser()

        // Act
        sut.resetToIdle()
    }
}
