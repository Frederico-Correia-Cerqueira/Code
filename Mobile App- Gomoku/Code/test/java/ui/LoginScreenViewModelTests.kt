package ui

import com.example.gomoku.LoginService
import com.example.gomoku.domain.*
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.login.LoginScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val testLoginDto = LoginDto("111-222-333", 1)
    private val loginService = mockk<LoginService>()
    private val userDataStore = mockk<UserDataStore> {
        coEvery { updateUserInfo(any()) } returns Unit
    }

    @Test
    fun initially_the_login_state_flow_is_idle() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)

        // Act
        val gate = SuspendingGate()
        var collectedState: IOState<LoginDto?>? = null
        val collectJob = launch {
            sut.loginDto.collect {
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

    @Test
    fun fetchLogin_emits_to_the_login_state_flow_the_loaded_state() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)
        coEvery { loginService.fetchLogin(any(), any()) } returns testLoginDto


        // Act
        val gate = SuspendingGate()
        var lastCollectedState: IOState<LoginDto?>? = null
        val collectJob = launch {
            sut.loginDto.collect {
                if (it is Loaded) {
                    lastCollectedState = it
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchLogin()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Assert
        Assert.assertNotNull(
            "Expected Loaded but got $lastCollectedState instead",
            lastCollectedState is Loaded
        )
    }

    @Test
    fun fetchLogin_emits_to_the_login_state_flow_the_loading_state() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)
        coEvery { loginService.fetchLogin(any(), any()) } returns testLoginDto

        // Act
        val gate = SuspendingGate()
        var lastCollectedState: IOState<LoginDto?>? = null
        val collectJob = launch {
            sut.loginDto.collect {
                if (it is Loading) {
                    lastCollectedState = it
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchLogin()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }

        // Assert
        Assert.assertNotNull(
            "Expected Loading but got $lastCollectedState instead",
            lastCollectedState is Loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun fetchLogin_throws_if_state_is_not_idle() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)
        sut.setName("username")
        sut.setPassword("password")
        sut.fetchLogin()

        // Act
        sut.fetchLogin()
    }

    @Test
    fun resetToIdle_sets_state_to_idle() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)
        coEvery { loginService.fetchLogin(any(), any()) } returns testLoginDto


        // Act
        val gate = SuspendingGate()
        val collectJob = launch {
            sut.loginDto.collect {
                if (it is Loaded) {
                    gate.open()
                }
            }
        }

        sut.setName("username")
        sut.setPassword("password")
        sut.fetchLogin()

        // Wait for the flow to emit the latest value
        withTimeout(1000) {
            gate.await()
            collectJob.cancelAndJoin()
        }
        // Act
        sut.resetToIdle()

        // Assert
        val currentState = sut.loginDto.first()
        Assert.assertNotNull(
            "Expected Loaded but got $currentState instead",
            currentState is Idle
        )
    }

    @Test(expected = IllegalStateException::class)
    fun resetToIdle_throws_if_state_is_not_loaded() = runTest {
        // Arrange
        val sut = LoginScreenViewModel(loginService, userDataStore)
        sut.setName("username")
        sut.setPassword("password")
        sut.fetchLogin()

        // Act
        sut.resetToIdle()
    }
}
