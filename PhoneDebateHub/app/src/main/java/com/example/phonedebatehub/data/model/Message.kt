package com.example.phonedebatehub.data.model

data class Message(
    val id: String,                 // was Long
    val debateId: Long,
    val author: String,
    val content: String,
    val createdAt: String?,
    val likes: Int = 0,
    val userLiked: Boolean = false
)
