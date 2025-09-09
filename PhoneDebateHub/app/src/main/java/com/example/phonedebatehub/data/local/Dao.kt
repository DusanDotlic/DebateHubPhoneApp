package com.example.phonedebatehub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DebateDao {
    @Query("SELECT * FROM debates ORDER BY updatedAt DESC")
    suspend fun getAll(): List<DebateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DebateEntity>)
}

//These are te Data Access Objects
//DAOs provide type-safe access

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MessageEntity?        // <-- String

    @Query("DELETE FROM messages WHERE debateId = :debateId")
    suspend fun deleteForDebate(debateId: Long)

    @Query("SELECT * FROM messages WHERE debateId = :debateId ORDER BY createdAt ASC")
    suspend fun listByDebate(debateId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MessageEntity>)

    @Update
    suspend fun update(item: MessageEntity)


}
