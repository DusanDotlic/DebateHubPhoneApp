package com.example.phonedebatehub.data.mapper

import com.example.phonedebatehub.data.local.MessageEntity
import com.example.phonedebatehub.data.model.Message
import com.example.phonedebatehub.network.MessageDto

// Network -> Domain
fun MessageDto.toDomain(): Message = Message(
    id = id,
    debateId = debateId,
    author = author ?: "Unknown",
    content = content ?: "",
    createdAt = createdAt,
    likes = likes ?: 0,
    userLiked = userLiked ?: false
)


// Room -> Domain
fun MessageEntity.toDomain(): Message = Message(
    id = id,
    debateId = debateId,
    author = author,
    content = content,
    createdAt = createdAt,
    likes = likes,
    userLiked = userLiked
)

// Domain -> Room
fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    debateId = debateId,
    author = author,
    content = content,
    createdAt = createdAt,
    likes = likes,
    userLiked = userLiked
)

// Network -> Room
fun MessageDto.toEntity(): MessageEntity = MessageEntity(
    id = id,
    debateId = debateId,
    author = author ?: "Unknown",
    content = content ?: "",
    createdAt = createdAt,
    likes = likes ?: 0,
    userLiked = userLiked ?: false
)