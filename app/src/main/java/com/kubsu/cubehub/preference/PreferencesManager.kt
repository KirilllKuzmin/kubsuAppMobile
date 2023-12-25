package com.kubsu.cubehub.preference

import android.content.Context

class PreferencesManager(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)

    fun saveUsername (username: String?) {
        sharedPreferences
            .edit()
            .putString("username", username)
            .apply()
    }

    fun savePassword (password: String?) {
        sharedPreferences
            .edit()
            .putString("password", password)
            .apply()
    }

    fun saveToken (token: String?) {
        sharedPreferences
            .edit()
            .putString("token", token)
            .apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }
}