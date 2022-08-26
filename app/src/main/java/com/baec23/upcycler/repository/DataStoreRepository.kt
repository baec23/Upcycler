package com.baec23.upcycler.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.baec23.upcycler.navigation.Screen
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "upcycler_prefs")

@ActivityScoped
class DataStoreRepository @Inject constructor(
    private val context: Context
) {
    suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

//    suspend fun putInt(key: String, value: Int) {
//        val preferencesKey = intPreferencesKey(key)
//        context.dataStore.edit { preferences ->
//            preferences[preferencesKey] = value
//        }
//    }

    suspend fun putLong(key: String, value: Long){
        val preferencesKey = longPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun getString(key: String): String? {
        return try {
            val preferencesKey = stringPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[preferencesKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun remove(key: String){
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit{ preferences ->
            preferences.remove(preferencesKey)
        }
    }

//    suspend fun getInt(key: String): Int? {
//        return try {
//            val preferencesKey = intPreferencesKey(key)
//            val preferences = context.dataStore.data.first()
//            preferences[preferencesKey]
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    suspend fun getLong(key: String): Long? {
        return try {
            val preferencesKey = longPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[preferencesKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}