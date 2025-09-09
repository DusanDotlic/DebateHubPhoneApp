package com.example.phonedebatehub.network

/**
 * Body used when creating a new message (server assigns the id).
 */
data class MessageCreateDto(
    val debateId: Long,
    val author: String,
    val content: String,
    val createdAt: String,
    val likes: Int = 0,
    val userLiked: Boolean = false
)
