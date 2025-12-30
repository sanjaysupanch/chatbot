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

    private val _selectedChatId = MutableStateFlow<String?>(null)

    private val _chats = MutableStateFlow(
        listOf(
            Chat("1", "Support Bot", "Welcome!", System.currentTimeMillis(), 0),
            Chat("2", "Sales Assistant", "Check our offers.", System.currentTimeMillis(), 1),
            Chat("3", "Feedback Bot", null, null, 0)
        )
    )

    val uiState: StateFlow<ChatUiState> = combine(
        repository.messages,
        repository.isOnline,
        _selectedChatId,
        _chats
    ) { messages, isOnline, selectedId, chats ->

        val updatedChats = chats.map { chat ->
            val chatMessages = messages.filter { it.chatId == chat.id }
            val lastMsg = chatMessages.maxByOrNull { it.timestamp }

            if (lastMsg != null) {
                chat.copy(
                    lastMessage = lastMsg.content,
                    lastMessageTime = lastMsg.timestamp,
                    unreadCount = if (!lastMsg.isSentByUser) 1 else 0
                )
            } else chat
        }

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
            repository.sendMessage(content, currentChatId)
        }
    }

    override fun onCleared() {
        viewModelScope.launch { repository.disconnect() }
        super.onCleared()
    }
}