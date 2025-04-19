package com.example.trafficviolationpicturemanagementsystem.data.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences") // Name of the DataStore file
object UserPreferences{
    private val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
    private val USER_NAME = stringPreferencesKey("user_name")

    suspend fun saveLoginStatus(context: Context, isLoggedIn: Boolean, userName: String) {
        context.userDataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = isLoggedIn
            preferences[USER_NAME] = userName
        }
    }

    fun readLoginStatus(context: Context): Flow<Boolean> {
        return context.userDataStore.data.map { preferences ->
            preferences[IS_USER_LOGGED_IN] ?: false // 默认返回 false
        }
    }

    fun readUserName(context: Context): Flow<String> {
        return context.userDataStore.data.map { preferences ->
            preferences[USER_NAME] ?: "" // 默认返回空字符串
        }
    }

}