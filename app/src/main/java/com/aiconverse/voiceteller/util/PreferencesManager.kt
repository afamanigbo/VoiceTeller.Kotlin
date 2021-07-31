package com.aiconverse.voiceteller.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager private constructor(){

    companion object {
        private val preferencesManager = PreferencesManager()
        private lateinit var sharedPreferences: SharedPreferences

        fun getInstance(context: Context): PreferencesManager {
            if (!::sharedPreferences.isInitialized) {
                synchronized(PreferencesManager::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        val masterKey =
                            MasterKey.Builder(context)
                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                .build()
                        sharedPreferences = EncryptedSharedPreferences.create(context,
                            "USER_PREFS",
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
                    }
                }
            }
            return preferencesManager
        }
    }

    fun setItem(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getItem(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun clearAllItems() {
        sharedPreferences.edit().clear().apply()
    }
}