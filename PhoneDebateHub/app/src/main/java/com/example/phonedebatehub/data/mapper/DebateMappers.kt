package com.example.phonedebatehub.data.mapper

import com.example.phonedebatehub.data.local.DebateEntity
import com.example.phonedebatehub.data.model.Debate
import com.example.phonedebatehub.network.DebateDto

// Network -> Domain
fun DebateDto.toDomain() = Debate(
    id = id,
    title = title,
    updatedAt = updatedAt
)

// Room -> Domain
fun DebateEntity.toDomain() = Debate(
    id = id,
    title = title,
    updatedAt = updatedAt
)

// Domain -> Room
fun Debate.toEntity() = DebateEntity(
    id = id,
    title = title,
    updatedAt = updatedAt
)
