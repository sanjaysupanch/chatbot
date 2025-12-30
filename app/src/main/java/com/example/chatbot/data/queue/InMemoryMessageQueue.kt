package com.example.chatbot.data.queue

import com.example.chatbot.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryMessageQueue @Inject constructor() {

    private val _queue = MutableStateFlow<List<Message>>(emptyList())
    val queue = _queue.asStateFlow()

    fun add(message: Message) {
        val current = _queue.value.toMutableList()
        current.add(message)
        _queue.value = current
    }

    fun remove(messageId: String) {
        val current = _queue.value.toMutableList()
        current.removeAll { it.id == messageId }
        _queue.value = current
    }

    fun getAll(): List<Message> = _queue.value

    fun clear() {
        _queue.value = emptyList()
    }
}