package com.example.gomoku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.tags.LobbyTags
import com.example.gomoku.tags.LobbyTags.Companion.GAME_NORMAL_DETAIL_REQUEST_TAG
import com.example.gomoku.ui.lobby.LobbyScreen
import com.example.gomoku.ui.lobby.Options
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LobbyScreenTests {

    @get:Rule
    val composeTree = createComposeRule()

    @Test
    fun pressing_navigate_buttons_calls_the_requested_callback() {
        // Arrange
        var navigateRankingRequest = false
        var navigateSavedGamesRequest = false
        var navigatePlayRequest = false
        var navigateCreditsRequest = false
        var navigateProfileRequest = false
        composeTree.setContent {
            LobbyScreen(
                onDefinitionsRequest = {  },
                onRankingRequest = { navigateRankingRequest = true },
                onSavedGamesRequest = { navigateSavedGamesRequest = true },
                onPlayRequest = { navigatePlayRequest = true },
                onCreditsRequest = { navigateCreditsRequest = true },
                onProfileRequest = { navigateProfileRequest = true },
                onLogoutRequest = { },
                onGameSelect = { },
                onGameDetails = { },
                playerStats = Loaded(
                    Result.success(
                        Stats(
                            "Alice",
                            100.0,
                            1,
                            1,
                            2,
                            50,
                            Rank.PRO
                        )
                    )
                ),
                selected = Options.NORMAL,
                definitionPopup = false,
                displayDetails = "game explanation",
                enableToLogout = true
            )
        }
        // Act
        composeTree.onNodeWithTag(LobbyTags.RANKING_REQUEST_TAG).performClick()
        composeTree.onNodeWithTag(LobbyTags.SAVED_GAMES_REQUEST_TAG).performClick()
        composeTree.onNodeWithTag(LobbyTags.PLAY_REQUEST_TAG).performClick()
        composeTree.onNodeWithTag(LobbyTags.CREDITS_REQUEST_TAG).performClick()
        composeTree.onNodeWithTag(LobbyTags.PROFILE_REQUEST_TAG).performClick()
        // Assert
        assertTrue(navigateRankingRequest)
        assertTrue(navigateSavedGamesRequest)
        assertTrue(navigatePlayRequest)
        assertTrue(navigateCreditsRequest)
        assertTrue(navigateProfileRequest)
    }

    @Test
    fun selecting_game_changes_the_selected_game() {
        // Arrange
        var selectedGame: Options? = null
        composeTree.setContent {
            LobbyScreen(
                onDefinitionsRequest = {  },
                onRankingRequest = {  },
                onSavedGamesRequest = {  },
                onPlayRequest = {  },
                onCreditsRequest = {  },
                onProfileRequest = {  },
                onLogoutRequest = {  },
                onGameSelect = { selectedGame = it },
                onGameDetails = {  },
                playerStats = Loaded(
                    Result.success(
                        Stats(
                            "Alice",
                            100.0,
                            1,
                            1,
                            2,
                            50,
                            Rank.PRO
                        )
                    )
                ),
                selected = Options.NORMAL,
                definitionPopup = false,
                displayDetails = "Game explanation",
                enableToLogout = true
            )
        }
        // Act
        composeTree.onNodeWithTag(LobbyTags.GAME_NORMAL_REQUEST_TAG).performClick()
        // Assert
        assertEquals(Options.NORMAL, selectedGame)

        // Act
        composeTree.onNodeWithTag(LobbyTags.GAME_SWAP_REQUEST_TAG).performClick()
        // Assert
        assertEquals(Options.SWAP, selectedGame)

        // Act
        composeTree.onNodeWithTag(LobbyTags.GAME_SWAP_FIRST_MOVE_REQUEST_TAG).performClick()
        // Assert
        assertEquals(Options.SWAP_FIRST_MOVE, selectedGame)
    }

    @Test
    fun pressing_the_game_details_button_shows_the_game_detail_callback() {
        // Arrange
        var navigateGameDetails: Boolean by mutableStateOf(false)

        composeTree.setContent {
            LobbyScreen(
                onDefinitionsRequest = {  },
                onRankingRequest = {  },
                onSavedGamesRequest = {  },
                onPlayRequest = {  },
                onCreditsRequest = {  },
                onProfileRequest = {  },
                onLogoutRequest = {  },
                onGameSelect = {  },
                onGameDetails = { navigateGameDetails = !navigateGameDetails },
                playerStats = Loaded(
                    Result.success(
                        Stats(
                            "Alice",
                            100.0,
                            1,
                            1,
                            2,
                            50,
                            Rank.PRO
                        )
                    )
                ),
                selected = Options.NORMAL,
                definitionPopup = false,
                displayDetails = if(navigateGameDetails) "Game explanation" else null,
                enableToLogout = true
            )
        }
        // Act
        composeTree.onNodeWithTag(GAME_NORMAL_DETAIL_REQUEST_TAG).performClick()
        // Assert
        composeTree.onNodeWithTag(LobbyTags.GAME_DETAIL_TAG).assertExists()

        // Act
        composeTree.onNodeWithTag(GAME_NORMAL_DETAIL_REQUEST_TAG).performClick()
        // Assert
        composeTree.onNodeWithTag(LobbyTags.GAME_DETAIL_TAG).assertDoesNotExist()
    }


    @Test
    fun definitions_request_show_dialog() {
        // Arrange
        var popup: Boolean by mutableStateOf(false)

        composeTree.setContent {
            LobbyScreen(
                onDefinitionsRequest = { popup = !popup },
                onRankingRequest = {  },
                onSavedGamesRequest = {  },
                onPlayRequest = {  },
                onCreditsRequest = {  },
                onProfileRequest = {  },
                onLogoutRequest = {  },
                onGameSelect = {  },
                onGameDetails = {  },
                playerStats = Loaded(
                    Result.success(
                        Stats(
                            "Alice",
                            100.0,
                            1,
                            1,
                            2,
                            50,
                            Rank.PRO
                        )
                    )
                ),
                selected = Options.NORMAL,
                definitionPopup = popup,
                displayDetails = null,
                enableToLogout = true
            )
        }
        // Assert
        composeTree.onNodeWithTag(LobbyTags.DEFINITIONS_CARD_TAG).assertDoesNotExist()
        // Act
        composeTree.onNodeWithTag(LobbyTags.SETTINGS_REQUEST_TAG).performClick()
        // Assert
        composeTree.onNodeWithTag(LobbyTags.DEFINITIONS_CARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(LobbyTags.LOGOUT_REQUEST_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(LobbyTags.GO_BACK_REQUEST_TAG).assertIsDisplayed()

        // Act
        composeTree.onNodeWithTag(LobbyTags.GO_BACK_REQUEST_TAG).performClick()
        // Assert
        composeTree.onNodeWithTag(LobbyTags.DEFINITIONS_CARD_TAG).assertDoesNotExist()
    }
}