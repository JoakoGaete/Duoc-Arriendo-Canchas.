package com.example.uinavegacion.data.local.Storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences (private val context: Context){
    //declarar las key de guardado de datos de mi DataStore
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userIdKey = longPreferencesKey("user_id")
    private val profilePhotoKey = stringPreferencesKey("profile_photo")

    //funcion para modificar el valor de la variable Data Store
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = false
            prefs[userIdKey] = 0L // o prefs.remove(userIdKey) si quieres
        }
    }


    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }
    val userId: Flow<Long?> = context.dataStore.data
        .map { prefs ->
            prefs[userIdKey]
        }
    suspend fun setUserId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = id
        }
    }


    suspend fun setProfilePhoto(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[profilePhotoKey] = uri
        }
    }

    val profilePhotoUri: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[profilePhotoKey]
        }
    private val isAdminKey = booleanPreferencesKey("is_admin")

    val isAdmin: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isAdminKey] ?: false }

    suspend fun setAdmin(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isAdminKey] = value
        }
    }
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(isLoggedInKey)
            prefs.remove(userIdKey)
            prefs.remove(profilePhotoKey)
            prefs.remove(isAdminKey)
        }
    }

}