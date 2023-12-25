package com.kubsu.cubehub.data.network

import com.kubsu.cubehub.common.auth.AuthRequest
import com.kubsu.cubehub.common.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/authentication")
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>

    @POST("users/authentication")
    suspend fun authGetToken(@Body authRequest: AuthRequest): Response<com.kubsu.cubehub.data.model.User>
}