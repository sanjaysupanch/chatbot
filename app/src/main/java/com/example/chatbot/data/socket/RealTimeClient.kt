package com.example.chatbot.data.socket

import com.example.chatbot.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface RealTimeClient {
    val incomingMessages: Flow<Message>
    val connectionState: Flow<Boolean>

    suspend fun connect()
    suspend fun disconnect()
    suspend fun emitMessage(content: String, id: String, chatId: String)
}