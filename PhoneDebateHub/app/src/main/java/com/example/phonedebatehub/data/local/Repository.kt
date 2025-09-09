package com.example.phonedebatehub.data

import android.content.Context
import com.example.phonedebatehub.data.local.AppDatabase
import com.example.phonedebatehub.data.local.MessageEntity
import com.example.phonedebatehub.data.mapper.toDomain
import com.example.phonedebatehub.data.mapper.toEntity
import com.example.phonedebatehub.data.model.Debate
import com.example.phonedebatehub.data.model.Message
import com.example.phonedebatehub.network.MessageCreateDto
import com.example.phonedebatehub.network.RetrofitClient
import com.example.phonedebatehub.network.DebateCreateDto
import com.example.phonedebatehub.data.local.DebateEntity

class Repository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val api = RetrofitClient.api

    suspend fun getDebates(forceRefresh: Boolean): List<Debate> {
        val remote = api.getDebates()
        return remote.map { it.toDomain() }
    }

    suspend fun getMessages(debateId: Long, forceRefresh: Boolean): List<Message> {
        if (!forceRefresh) {
            val cached = db.messageDao().listByDebate(debateId)
            if (cached.isNotEmpty()) return cached.map { it.toDomain() }
        }

        val remote = api.getMessages(debateId).map { it.toDomain() }

        db.messageDao().deleteForDebate(debateId)
        db.messageDao().upsertAll(remote.map { it.toEntity() })
        return remote
    }

    suspend fun sendMessage(debateId: Long, author: String, content: String): Message {
        val createBody = MessageCreateDto(
            debateId = debateId,
            author = author,
            content = content,
            createdAt = System.currentTimeMillis().toString(),
            likes = 0,
            userLiked = false
        )

        val saved = api.postMessage(createBody).toDomain()

        db.messageDao().upsertAll(
            listOf(
                MessageEntity(
                    id = saved.id,
                    debateId = saved.debateId,
                    author = saved.author,
                    content = saved.content,
                    createdAt = saved.createdAt,
                    likes = saved.likes,
                    userLiked = saved.userLiked
                )
            )
        )
        return saved
    }

    suspend fun toggleLike(messageId: String): Message {       // <-- String
        val dao = db.messageDao()
        val current = dao.getById(messageId) ?: error("Message $messageId not found locally")

        val nowLiked = !current.userLiked
        val newLikes = (current.likes + if (nowLiked) 1 else -1).coerceAtLeast(0)

        // optimistic local update
        dao.update(current.copy(likes = newLikes, userLiked = nowLiked))

        // server sync
        val patched = api.patchMessage(
            id = messageId,
            body = mapOf("likes" to newLikes, "userLiked" to nowLiked)
        )

        val saved = patched.toEntity()
        dao.update(saved)
        return saved.toDomain()
    }




}
