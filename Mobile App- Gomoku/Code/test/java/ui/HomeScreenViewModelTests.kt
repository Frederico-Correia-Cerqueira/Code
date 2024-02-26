package ui

import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Loading
import com.example.gomoku.infrastructure.UserDataStore
import com.example.gomoku.ui.home.HomeScreenViewModel
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
class HomeScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val userDataStore = mockk<UserDataStore> {
        coEvery { getUserInfo() } coAnswers {
            DataStoreDto("testToken", 0, "testUsername")
        }
    }
    private val sut = HomeScreenViewModel(userDataStore)

    @Test
    fun `initially the loggedIn mutableState state is idle`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.loggedIn} instead", sut.loggedIn is Idle)
    }

    @Test
    fun `isLoggedIn emits to the loggedIn mutableState the loaded state`() = runTest {
        // Arrange
        // Act
        var state: IOState<Boolean?>? = null
        sut.isLoggedIn()
        if (sut.loggedIn is Loading) {
            state = sut.loggedIn
        }

        Assert.assertNotNull(
            "Expected Loaded but got $state instead",
            state
        )
    }

    @Test
    fun `isLoggedIn emits to the loggedIn mutableState the loading state`() = runTest {
        // Arrange
        // Act
        sut.isLoggedIn()
        // Assert
        val loading = sut.loggedIn as? Loading
        Assert.assertNotNull(
            "Expected Loaded but got ${sut.loggedIn} instead",
            loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `isLoggedIn throws if state is not idle`() = runTest {
        // Arrange
        sut.isLoggedIn()
        // Act
        sut.isLoggedIn()
    }
}
