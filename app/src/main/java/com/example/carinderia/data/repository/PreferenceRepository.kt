package com.example.carinderia.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.carinderia.core.constants.JsonRepository
import com.example.carinderia.core.constants.PreferenceKey.KEY_CONFIG
import com.example.carinderia.core.constants.PreferenceKey.KEY_USER_PROFILE
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: JsonRepository
) {
    private val Context.dataStore by preferencesDataStore(KEY_CONFIG)
    private val userProfile = stringPreferencesKey(KEY_USER_PROFILE)
    val isLogin = context.dataStore.data.map { it[userProfile] != null }
    
    val userProfileFlow = context.dataStore.data
        .mapNotNull { it[userProfile] }
        .map { json.decodeFromString<User>(it) }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    suspend fun clearPreference() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { it.clear() }
        }
    }

    suspend fun setProfile(profile: User) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[userProfile] = json.encodeToString(profile)
            }
        }
    }
}