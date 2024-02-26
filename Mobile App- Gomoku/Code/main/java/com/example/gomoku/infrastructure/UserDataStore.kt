package com.example.gomoku.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.gomoku.domain.DataStoreDto
import com.example.gomoku.domain.UserInfoRepository
import kotlinx.coroutines.flow.first

private const val USER_TOKEN_KEY = "Token"
private const val USER_ID_KEY = "UserID"
private const val USER_NAME_KEY = "UserName"

/**
 * A user information repository implementation supported in DataStore, the
 * modern alternative to SharedPreferences.
 */
class UserDataStore(private val store: DataStore<Preferences>) : UserInfoRepository {

    private val tokenKey = stringPreferencesKey(USER_TOKEN_KEY)
    private val userIDKey = intPreferencesKey(USER_ID_KEY)
    private val userName = stringPreferencesKey(USER_NAME_KEY)

    override suspend fun getUserInfo(): DataStoreDto? {
        val preferences = store.data.first()
        val token = preferences[tokenKey]
        val userID = preferences[userIDKey]
        val userName = preferences[userName]
        return if (token != null && userID != null && userName != null)
            DataStoreDto(token, userID, userName)
        else
            null
    }

    override suspend fun updateUserInfo(userInfo: DataStoreDto) {
        store.edit { preferences ->
            preferences[tokenKey] = userInfo.token
            preferences[userIDKey] = userInfo.userID
            preferences[userName] = userInfo.userName
        }
    }

    override suspend fun removeUserInfo() {
        store.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(userIDKey)
            preferences.remove(userName)
        }
    }
}