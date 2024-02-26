package ui

import com.example.gomoku.InformationService
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Information
import com.example.gomoku.domain.Loading
import com.example.gomoku.ui.credits.CreditsScreenViewModel

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
class CreditsScreenViewModelTests {

    @get:Rule
    val rule = MockMainDispatcherRule(testDispatcher = StandardTestDispatcher())

    private val informationService = mockk<InformationService> {
        coEvery { fetchCredits() } coAnswers {
            Information("testVersion", emptyList())
        }
    }
    private val sut = CreditsScreenViewModel(informationService)

    @Test
    fun `initially the info mutableState state is idle`() = runTest {
        // Arrange
        // Act
        // Assert
        Assert.assertTrue("Expected Idle but got ${sut.info} instead", sut.info is Idle)
    }

    @Test
    fun `fetchAuthors emits to the info mutableState the loaded state`() = runTest {
        // Arrange
        // Act
        var state: IOState<Information?>? = null
        sut.fetchAuthors()
        if (sut.info is Loading) {
            state = sut.info
        }

        Assert.assertNotNull(
            "Expected Loaded but got $state instead",
            state
        )
    }

    @Test
    fun `fetchAuthors emits to the info mutableState the loading state`() = runTest {
        // Arrange
        // Act
        sut.fetchAuthors()
        // Assert
        val loading = sut.info as? Loading
        Assert.assertNotNull(
            "Expected Loaded but got ${sut.info} instead",
            loading
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `fetchAuthors throws if state is not idle`() = runTest {
        // Arrange
        sut.fetchAuthors()
        // Act
        sut.fetchAuthors()
    }
}
