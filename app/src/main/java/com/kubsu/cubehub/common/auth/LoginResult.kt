package com.kubsu.cubehub.common.auth

data class LoginResult(
    val success: Boolean = false,
    val token: String? = null
)
