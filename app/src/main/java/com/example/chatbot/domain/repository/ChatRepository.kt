package com.example.chatbot.domain.repository

import com.example.chatbot.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    val messages: Flow<List<Message>>

    val isOnline: Flow<Boolean>

    suspend fun connect()
    suspend fun disconnect()
    suspend fun sendMessage(content: String, chatId: String)
}