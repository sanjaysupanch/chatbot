package com.example.chatbot.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.domain.model.Chat
import com.example.chatbot.domain.repository.ChatRepository
import com.example.chatbot.presentation.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    // Internal state to track navigation
    private val _selectedChatId = MutableStateFlow<String?>(null)

    // Internal source of "Chats" (In a real app, this might come from a DB)
    // We initialize it with the dummy bots required for the assignment
    private val _chats = MutableStateFlow(
        listOf(
            Chat("1", "Support Bot", "Welcome!", System.currentTimeMillis(), 0),
            Chat("2", "Sales Assistant", "Check our offers.", System.currentTimeMillis(), 1),
            Chat("3", "Feedback Bot", null, null, 0)
        )
    )

    // Combine all data sources into one UI State
    val uiState: StateFlow<ChatUiState> = combine(
        repository.messages,      // Stream of all messages (Socket + Queue)
        repository.isOnline,      // Network status
        _selectedChatId,          // Navigation state
        _chats                    // List of Chat items
    ) { messages, isOnline, selectedId, chats ->

        // 1. Update Chat Previews dynamically based on latest messages
        val updatedChats = chats.map { chat ->
            val chatMessages = messages.filter { it.chatId == chat.id }
            val lastMsg = chatMessages.maxByOrNull { it.timestamp }

            if (lastMsg != null) {
                chat.copy(
                    lastMessage = lastMsg.content,
                    lastMessageTime = lastMsg.timestamp,
                    // Simple unread logic: If last msg not from me, count as 1 (for demo)
                    unreadCount = if (!lastMsg.isSentByUser) 1 else 0
                )
            } else chat
        }

        // 2. Filter messages for the active conversation
        val activeMessages = if (selectedId != null) {
            messages.filter { it.chatId == selectedId }.sortedBy { it.timestamp }
        } else {
            emptyList()
        }

        ChatUiState(
            chats = updatedChats,
            selectedChatId = selectedId,
            activeMessages = activeMessages,
            isOnline = isOnline
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState())

    init {
        viewModelScope.launch { repository.connect() }
    }

    // --- User Actions ---

    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
    }

    fun closeChat() {
        _selectedChatId.value = null
    }

    fun sendMessage(content: String) {
        val currentChatId = _selectedChatId.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            // The Repository handles the Queue/Socket logic
            // We just need to pass the content and the target Chat ID
            repository.sendMessage(content, currentChatId)
        }
    }

    override fun onCleared() {
        viewModelScope.launch { repository.disconnect() }
        super.onCleared()
    }
}