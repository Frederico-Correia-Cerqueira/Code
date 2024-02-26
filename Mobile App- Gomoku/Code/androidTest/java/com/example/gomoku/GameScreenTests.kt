package com.example.gomoku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.gomoku.domain.Board
import com.example.gomoku.domain.BoardTypes
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.OpeningRules
import com.example.gomoku.domain.Pieces
import com.example.gomoku.domain.PlayerInfo
import com.example.gomoku.domain.SquareDetails
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.Variants
import com.example.gomoku.tags.GameTags
import com.example.gomoku.ui.game.GameScreen
import org.junit.Rule
import org.junit.Test

class GameScreenTests {

    @get:Rule
    val composeTree = createComposeRule()

    @Test
    fun play_and_square_stay_disabled() {
        var game by mutableStateOf(
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(0-0)" to SquareDetails(2, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 1
                        ),
                        Variants.NORMAL,
                        OpeningRules.NORMAL,
                        false
                    )
                )
            )
        )
        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ ->
                    game = Loaded(
                        Result.success(
                            game.value.getOrNull()!!.copy(
                                board = Board(
                                    moves = mapOf(
                                        "(1-1)" to SquareDetails(1, Pieces.N)
                                    ),
                                    type = BoardTypes.BOARD_RUN,
                                    turn = 2
                                )
                            )
                        )
                    )
                },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_CELL_TAG + "_" + 1 + "_" + 1)
            .assertIsEnabled()
        // Act
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_CELL_TAG + "_" + 1 + "_" + 1).performClick()
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_CELL_TAG + "_" + 1 + "_" + 1)
            .assertIsNotEnabled()


    }

    @Test
    fun cant_play_at_non_nullable_square_player() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-1)" to SquareDetails(1, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 2
                        ),
                        Variants.NORMAL,
                        OpeningRules.NORMAL,
                        false
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_CELL_TAG + "_" + 1 + "_" + 2)
            .assertIsEnabled()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_CELL_TAG + "_" + 1 + "_" + 1)
            .assertIsNotEnabled()
    }

    @Test
    fun check_game_components_displayed_while_board_run() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-1)" to SquareDetails(1, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 2
                        ),
                        Variants.NORMAL,
                        OpeningRules.NORMAL,
                        false
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertDoesNotExist()
    }

    @Test
    fun check_game_components_displayed_while_board_win() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-1)" to SquareDetails(1, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_WIN,
                            turn = 2
                        ),
                        Variants.NORMAL,
                        OpeningRules.NORMAL,
                        false
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG).assertIsDisplayed()
    }

    @Test
    fun check_game_components_displayed_while_board_run_and_info_true_for_swap() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-2)" to SquareDetails(2, Pieces.N),
                                "(1-3)" to SquareDetails(1, Pieces.N),
                                "(1-4)" to SquareDetails(2, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 1
                        ),
                        Variants.NORMAL,
                        OpeningRules.SWAP,
                        true
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG).assertDoesNotExist()

        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_YES_BUTTON_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_NO_BUTTON_TAG).assertIsDisplayed()
    }

    @Test
    fun check_game_components_displayed_while_board_run_and_info_false_for_swap() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-2)" to SquareDetails(2, Pieces.N),
                                "(1-3)" to SquareDetails(1, Pieces.N),
                                "(1-4)" to SquareDetails(2, Pieces.N)
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 1
                        ),
                        Variants.NORMAL,
                        OpeningRules.SWAP,
                        false
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG).assertDoesNotExist()

        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_YES_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_NO_BUTTON_TAG).assertDoesNotExist()
    }

    @Test
    fun check_game_components_displayed_while_board_run_and_info_true_for_swap_first_move() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-2)" to SquareDetails(2, Pieces.N),
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 1
                        ),
                        Variants.SWAP_FIRST_MOVE,
                        OpeningRules.SWAP,
                        true
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                descriptionGame = "Normal",
                enableToPlay = true,
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG).assertDoesNotExist()

        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_YES_BUTTON_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_NO_BUTTON_TAG).assertIsDisplayed()
    }

    @Test
    fun check_game_components_displayed_while_board_run_and_info_false_for_swap_first_move() {
        val game =
            Loaded(
                Result.success(
                    GameDto(
                        id = 1,
                        2,
                        1,
                        Board(
                            moves = mapOf(
                                "(1-2)" to SquareDetails(2, Pieces.N),
                            ),
                            type = BoardTypes.BOARD_RUN,
                            turn = 1
                        ),
                        Variants.SWAP_FIRST_MOVE,
                        OpeningRules.SWAP,
                        false
                    )
                )
            )

        // Arrange
        composeTree.setContent {
            GameScreen(
                game = game,
                current = PlayerInfo(1, "Alice"),
                opponent = Loaded(
                    Result.success(
                        UserDto(2, "Alice", "", "IN_GAME")
                    )
                ),
                onSquareClick = { _, _ -> },
                onSwapResponseClick = { },
                onBackLobbyClick = { },
                nameGame = "Normal",
                enableToPlay = true,
                descriptionGame = "Normal",
                setNameGame = { },
                setDescriptionGame = { },
                saveGame = false,
                setSaveGame = { },
                onSaveGame = { }
            )
        }
        // Assert
        composeTree.onNodeWithTag(GameTags.GAME_CURRENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_OPPONENT_NAME_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_BOARD_TAG).assertIsDisplayed()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_GO_BACK_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_WIN_POPUP_SAVE_BUTTON_TAG).assertDoesNotExist()

        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_YES_BUTTON_TAG).assertDoesNotExist()
        composeTree.onNodeWithTag(GameTags.GAME_SWAP_POPUP_NO_BUTTON_TAG).assertDoesNotExist()
    }
}