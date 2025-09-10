package com.example.phonedebatehub.data.local

import android.content.Context
import com.example.phonedebatehub.data.local.AppDatabase
import com.example.phonedebatehub.data.local.MessageEntity
import com.example.phonedebatehub.network.RetrofitClient
import com.example.phonedebatehub.data.model.Debate
import com.example.phonedebatehub.data.mapper.toDomain
import com.example.phonedebatehub.data.mapper.toEntity
import com.example.phonedebatehub.data.model.Message
import com.example.phonedebatehub.network.MessageCreateDto

class Repository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val api = RetrofitClient.api

    suspend fun getDebates(forceRefresh: Boolean): List<Debate> {
        val remote = api.getDebates()
        return remote.map { it.toDomain() }
    }

    suspend fun getMessages(debateId: Long, forceRefresh: Boolean): List<Message> {
        // 1) local
        if (!forceRefresh) {
            val cached = db.messageDao().listByDebate(debateId)
            if (cached.isNotEmpty()) return cached.map { it.toDomain() }
        }

        // 2) remote
        val remoteDtos = api.getMessages(debateId)

        // 3) save RAW epoch to DB (from DTO)
        db.messageDao().deleteForDebate(debateId)
        db.messageDao().upsertAll(remoteDtos.map { it.toEntity() })

        // 4) return formatted domain
        return remoteDtos.map { it.toDomain() }
    }

    suspend fun sendMessage(debateId: Long, author: String, content: String): Message {
        // send raw epoch to server
        val createBody = MessageCreateDto(
            debateId = debateId,
            author = author,
            content = content,
            createdAt = System.currentTimeMillis().toString(),
            likes = 0,
            userLiked = false
        )

        // server returns a DTO with raw epoch; format for UI, store raw in DB
        val savedDto = api.postMessage(createBody)
        val savedDomain = savedDto.toDomain()

        db.messageDao().upsertAll(listOf(savedDto.toEntity()))

        return savedDomain
    }

    suspend fun toggleLike(messageId: String): Message {
        val dao = db.messageDao()
        val current = dao.getById(messageId) ?: error("Message $messageId not found locally")

        val nowLiked = !current.userLiked
        val newLikes = (current.likes + if (nowLiked) 1 else -1).coerceAtLeast(0)

        // 1) optimistic
        val optimistic = current.copy(likes = newLikes, userLiked = nowLiked)
        dao.update(optimistic)

        return try {
            // 2) network
            val patchedDto = api.patchMessage(
                id = messageId,
                body = mapOf("likes" to newLikes, "userLiked" to nowLiked)
            )
            // 3) persist RAW epoch
            val savedEntity = patchedDto.toEntity()
            dao.update(savedEntity)

            // 4) to domain (formatted)
            savedEntity.toDomain()
        } catch (t: Throwable) {
            // revert
            dao.update(current)
            throw t
        }
    }
}
