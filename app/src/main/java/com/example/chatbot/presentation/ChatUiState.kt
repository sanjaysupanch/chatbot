package com.example.chatbot.presentation

import com.example.chatbot.domain.model.Chat
import com.example.chatbot.domain.model.Message

data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val selectedChatId: String? = null,
    val activeMessages: List<Message> = emptyList(),
    val isOnline: Boolean = false,
    val isLoading: Boolean = false
)