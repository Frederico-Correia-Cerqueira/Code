package com.example.gomoku.tags

class LobbyTags {
    companion object {
        // App's tag
        const val TAG = "LOBBY_APP_TAG"

        // Navigation's request tags
        const val PLAY_REQUEST_TAG = "PLAY_REQUEST_TAG"
        const val PROFILE_REQUEST_TAG = "PROFILE_REQUEST_TAG"
        const val RANKING_REQUEST_TAG = "RANKING_REQUEST_TAG"
        const val CREDITS_REQUEST_TAG = "CREDITS_REQUEST_TAG"
        const val SETTINGS_REQUEST_TAG = "SETTINGS_REQUEST_TAG"
        const val SAVED_GAMES_REQUEST_TAG = "SAVED_GAMES_REQUEST_TAG"

        // Game's List request tags
        const val GAME_NORMAL_REQUEST_TAG = "GAME_NORMAL_REQUEST_TAG"
        const val GAME_SWAP_REQUEST_TAG = "GAME_SWAP_REQUEST_TAG"
        const val GAME_SWAP_FIRST_MOVE_REQUEST_TAG = "GAME_SWAP_FIRST_MOVE_REQUEST_TAG"

        // Game's Details request tags
        const val GAME_DETAIL_TAG = "GAME_DETAIL_TAG"
        const val GAME_NORMAL_DETAIL_REQUEST_TAG = "GAME_NORMAL_DETAIL_REQUEST_TAG"
        const val GAME_SWAP_DETAIL_REQUEST_TAG = "GAME_SWAP_DETAIL_REQUEST_TAG"
        const val GAME_SWAP_FIRST_MOVE_DETAIL_REQUEST_TAG =
            "GAME_SWAP_FIRST_MOVE_DETAIL_REQUEST_TAG"

        // Definitions request tags
        const val DEFINITIONS_CARD_TAG = "DEFINITIONS_CARD_TAG"
        const val GO_BACK_REQUEST_TAG = "DEFINITIONS_REQUEST_TAG"
        const val LOGOUT_REQUEST_TAG = "LOGOUT_REQUEST_TAG"

    }
}