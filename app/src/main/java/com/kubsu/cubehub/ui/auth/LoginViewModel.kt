package com.kubsu.cubehub.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun setToken(token: String) {
        _token.value = token
    }
}