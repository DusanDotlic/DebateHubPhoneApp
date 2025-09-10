package com.example.phonedebatehub.data.mapper

import com.example.phonedebatehub.data.local.MessageEntity
import com.example.phonedebatehub.data.model.Message
import com.example.phonedebatehub.network.MessageDto
import com.example.phonedebatehub.util.DateFormatter

// Network -> Domain (format createdAt for display)
fun MessageDto.toDomain(): Message = Message(
    id = id,
    debateId = debateId,
    author = author ?: "Unknown",
    content = content ?: "",
    createdAt = DateFormatter.epochStringToDisplay(createdAt),
    likes = likes ?: 0,
    userLiked = userLiked ?: false
)

// Room -> Domain (format at read time; keep storage raw epoch)
fun MessageEntity.toDomain(): Message = Message(
    id = id,
    debateId = debateId,
    author = author,
    content = content,
    createdAt = DateFormatter.epochStringToDisplay(createdAt),
    likes = likes,
    userLiked = userLiked
)

// Domain -> Room
// NOTE: Domain.createdAt is formatted for UI; do NOT write it back to DB.
// Callers who need to persist should use the DTO->Entity mapper below
// or pass a raw epoch for createdAt.
fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    debateId = debateId,
    author = author,
    content = content,
    // We don't know the raw epoch here; keep what's already in Message.createdAt if it's epoch,
    // otherwise store as-is (harmless for display-only flows).
    createdAt = createdAt,
    likes = likes,
    userLiked = userLiked
)

// Network -> Room (keeps RAW epoch so we can always re-format on read)
fun MessageDto.toEntity(): MessageEntity = MessageEntity(
    id = id,
    debateId = debateId,
    author = author ?: "Unknown",
    content = content ?: "",
    createdAt = createdAt,        // raw epoch string
    likes = likes ?: 0,
    userLiked = userLiked ?: false
)
