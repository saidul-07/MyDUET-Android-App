package com.example.myduet.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class PreferenceManager(private val context: Context) {

    companion object {
        private val KEY_DEPT = stringPreferencesKey("department")
        private val KEY_YEAR = stringPreferencesKey("year")
        private val KEY_SECTION = stringPreferencesKey("section")
    }

    suspend fun saveAcademicProfile(dept: String, year: String, section: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEPT] = dept
            preferences[KEY_YEAR] = year
            preferences[KEY_SECTION] = section
        }
    }

    val department: Flow<String?> = context.dataStore.data.map { it[KEY_DEPT] }
    val year: Flow<String?> = context.dataStore.data.map { it[KEY_YEAR] }
    val section: Flow<String?> = context.dataStore.data.map { it[KEY_SECTION] }
}