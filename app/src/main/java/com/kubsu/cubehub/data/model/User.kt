package com.kubsu.cubehub.data.model

data class User(

    val token: String,

    val userId: Long,

    val username: String,

    val roles: List<String>
)
