package com.example.phonedebatehub.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

data class DebateDto(
    val id: Long,
    val title: String,
    val updatedAt: String
)

data class MessageDto(
    val id: String,                // <-- String
    val debateId: Long,
    val author: String?,
    val content: String?,
    val createdAt: String?,
    val likes: Int? = 0,
    val userLiked: Boolean? = false
)



interface ApiService {
    @GET("debates")
    suspend fun getDebates(): List<DebateDto>


    @GET("messages")
    suspend fun getMessages(@Query("debateId") debateId: Long): List<MessageDto>


    @POST("messages")
    suspend fun postMessage(@Body body: MessageCreateDto): MessageDto


    @PATCH("messages/{id}")
    suspend fun patchMessage(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): MessageDto

/*  //Creating debates
    @POST("debates")
    suspend fun postDebate(@Body body: DebateCreateDto): DebateDto
*/

}
