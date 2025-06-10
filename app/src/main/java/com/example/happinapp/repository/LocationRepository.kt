package com.example.happinapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocationRepository(private val context: Context) {

    // Define a key for storing the user's selected location string.
    private val USER_LOCATION_KEY = stringPreferencesKey("user_location")

    /**
     * A Flow that emits the user's saved location whenever it changes.
     * It will emit null if no location has been saved yet.
     */
    val getLocation: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_LOCATION_KEY]
        }

    /**
     * Saves the user's chosen location to DataStore.
     * This is a suspend function, so it must be called from a coroutine.
     *
     * @param location The string name of the location to save (e.g., "Hyderabad").
     */
    suspend fun saveLocation(location: String) {
        context.dataStore.edit { settings ->
            settings[USER_LOCATION_KEY] = location
        }
    }
}