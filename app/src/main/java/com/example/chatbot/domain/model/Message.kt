package com.example.chatbot.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val content: String,
    val timestamp: Long,
    val isSentByUser: Boolean,
    val status: MessageStatus
)

enum class MessageStatus {
    PENDING,
    SENT,
    FAILED
}