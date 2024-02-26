package com.example.gomoku.domain


/**
 * Contract to be supported by the user info repository.
 * The user repository is the authority for the user info application state.
 */
interface UserInfoRepository {
    /**
     * Gets the user info if it exists, null otherwise.
     */
    suspend fun getUserInfo(): DataStoreDto?

    /**
     * Updates the user info.
     */
    suspend fun updateUserInfo(userInfo: DataStoreDto)

    /**
     * Removes the user info.
     */
    suspend fun removeUserInfo()
}