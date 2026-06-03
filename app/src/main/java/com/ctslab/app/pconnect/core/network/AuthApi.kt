package com.avis.app.ptalk.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// --- Request Schemas matching central auth service ---

data class CentralRegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_type") val userType: String = "account_owner",
    @SerializedName("display_name") val displayName: String? = null
)

data class CentralLoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("device_info") val deviceInfo: String? = null
)

// --- Response Schemas matching central auth service ---

data class CentralTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer",
    @SerializedName("expires_in") val expiresIn: Int
)

data class CentralUserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("user_type") val userType: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("subscription_tier") val subscriptionTier: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_superuser") val isSuperuser: Boolean
)

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: CentralRegisterRequest): CentralUserResponse

    @POST("auth/login")
    suspend fun login(@Body request: CentralLoginRequest): CentralTokenResponse

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") bearerToken: String): CentralUserResponse
}
