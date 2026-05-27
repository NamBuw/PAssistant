package com.avis.app.ptalk.core.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// --- Device Registration ---

data class DeviceRegisterRequest(
    val mac_address: String,
    val serial_number: String? = null,
    val model: String? = null,
    val device_type: Int? = null,
    val firmware_version: String? = null
)

data class DeviceRegisterResponse(
    val success: Boolean,
    val deviceId: String? = null,
    val message: String? = null,
    val error: String? = null
)

// --- Chat Sessions ---

data class ChatSessionResponse(
    val id: String,
    val userId: String,
    val deviceId: String?,
    val productSource: String,
    val channel: String,
    val title: String?,
    val messageCount: Int,
    val avgSentiment: String?,
    val startedAt: String,
    val lastMessageAt: String?,
    val userName: String?,
    val deviceLabel: String?
)

data class ChatSessionsListResponse(
    val sessions: List<ChatSessionResponse>,
    val pagination: PaginationResponse
)

data class CreateChatSessionRequest(
    val user_id: String? = null,
    val device_id: String? = null,
    val product_source: String,
    val channel: String,
    val title: String? = null
)

data class CreateChatSessionResponse(
    val success: Boolean,
    val sessionId: String? = null,
    val startedAt: String? = null
)

// --- Chat Messages ---

data class ChatMessageResponse(
    val id: String,
    val sender: String,
    val messageType: String,
    val content: String,
    val audioUrl: String?,
    val audioDuration: Int?,
    val sentiment: String?,
    val emotionCode: String?,
    val createdAt: String
)

data class ChatMessagesListResponse(
    val sessionId: String,
    val messages: List<ChatMessageResponse>,
    val pagination: PaginationResponse
)

data class CreateChatMessageRequest(
    val session_id: String,
    val sender: String,
    val content: String,
    val message_type: String? = null,
    val audio_url: String? = null,
    val sentiment: String? = null,
    val emotion_code: String? = null
)

data class CreateChatMessageResponse(
    val success: Boolean,
    val messageId: String? = null,
    val createdAt: String? = null
)

// --- Pagination ---

data class PaginationResponse(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

// --- Device-User Linking ---

data class DeviceUserLinkRequest(
    val device_id: String,
    val user_id: String,
    val link_type: String = "viewer"
)

data class DeviceUserLinkResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * Dashboard API for device registration, chat history, and device-user linking.
 * Base URL: Dashboard backend (e.g., http://dashboard-host:3000/)
 */
interface DashboardApi {

    @POST("api/v1/devices/register")
    suspend fun registerDevice(
        @Body request: DeviceRegisterRequest
    ): DeviceRegisterResponse

    @GET("api/v1/chat/sessions")
    suspend fun getChatSessions(
        @Query("product_source") productSource: String? = null,
        @Query("channel") channel: String? = null,
        @Query("device_id") deviceId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ChatSessionsListResponse

    @POST("api/v1/chat/sessions")
    suspend fun createChatSession(
        @Body request: CreateChatSessionRequest
    ): CreateChatSessionResponse

    @GET("api/v1/chat/messages")
    suspend fun getChatMessages(
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ChatMessagesListResponse

    @POST("api/v1/chat/messages")
    suspend fun createChatMessage(
        @Body request: CreateChatMessageRequest
    ): CreateChatMessageResponse

    @POST("api/v1/devices/link-user")
    suspend fun linkUserToDevice(
        @Body request: DeviceUserLinkRequest
    ): DeviceUserLinkResponse
}
