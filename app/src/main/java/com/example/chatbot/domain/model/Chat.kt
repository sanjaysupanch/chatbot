package com.example.chatbot.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Chat(
    val id: String,
    val name: String,
    val lastMessage: String?,
    val lastMessageTime: Long?,
    val unreadCount: Int
)