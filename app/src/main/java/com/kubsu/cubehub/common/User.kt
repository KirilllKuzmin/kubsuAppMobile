package com.kubsu.cubehub.common

import android.content.Context
import com.kubsu.cubehub.preference.PreferencesManager

object User {
    private var preferencesManager: PreferencesManager? = null

    var username: String?
        get() = preferencesManager?.getUsername()
        set(value) {
            preferencesManager?.saveUsername(value)
        }

    var password: String?
        get() = preferencesManager?.getPassword()
        set(value) {
            preferencesManager?.savePassword(value)
        }

    fun init (context: Context) {
        preferencesManager = PreferencesManager(context)
    }
}