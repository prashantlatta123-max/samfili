package com.prashantlatta.samfili.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "samfili_theme_prefs")

class ThemePreferences(private val context: Context) {
    companion object {
        val BG_IMAGE_URI = stringPreferencesKey("bg_image_uri")
        val BG_OPACITY = floatPreferencesKey("bg_opacity")
    }

    val bgImageUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[BG_IMAGE_URI]
    }

    val bgOpacity: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[BG_OPACITY] ?: 0.25f
    }

    suspend fun saveBackgroundImage(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[BG_IMAGE_URI] = uri
            } else {
                preferences.remove(BG_IMAGE_URI)
            }
        }
    }

    suspend fun saveOpacity(opacity: Float) {
        context.dataStore.edit { preferences ->
            preferences[BG_OPACITY] = opacity
        }
    }
}
