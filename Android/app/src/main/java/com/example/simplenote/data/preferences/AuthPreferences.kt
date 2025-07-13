package com.example.simplenote.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_preferences")

@Singleton
class AuthPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val TOKEN_EXPIRY_KEY = longPreferencesKey("token_expiry")
    }
    
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }
    
    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }
    
    val tokenExpiry: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_EXPIRY_KEY]
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long = 3600L) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[TOKEN_EXPIRY_KEY] = expiryTime
        }
    }
    
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }
    
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(TOKEN_EXPIRY_KEY)
        }
    }
    
    suspend fun isTokenExpired(): Boolean {
        val expiry = context.dataStore.data.map { preferences ->
            preferences[TOKEN_EXPIRY_KEY]
        }.first()
        
        return expiry == null || System.currentTimeMillis() >= expiry.toLong()
    }
    
    suspend fun shouldRefreshToken(): Boolean {
        val expiry = context.dataStore.data.map { preferences ->
            preferences[TOKEN_EXPIRY_KEY]
        }.first()
        
        // Refresh token if it expires in the next 5 minutes
        return expiry != null && System.currentTimeMillis() >= (expiry.toLong() - 300000)
    }
} 