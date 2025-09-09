package com.example.phonedebatehub.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//These are the Tables

@Entity(tableName = "debates")
data class DebateEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val updatedAt: String?
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,     // was Long
    val debateId: Long,
    val author: String,
    val content: String,
    val createdAt: String?,
    val likes: Int,
    val userLiked: Boolean
)



