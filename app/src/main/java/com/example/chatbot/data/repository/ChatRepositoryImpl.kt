package com.example.chatbot.data.repository

import com.example.chatbot.data.network.NetworkMonitor
import com.example.chatbot.data.queue.InMemoryMessageQueue
import com.example.chatbot.data.socket.RealTimeClient
import com.example.chatbot.domain.model.Message
import com.example.chatbot.domain.model.MessageStatus
import com.example.chatbot.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val realTimeClient: RealTimeClient,
    private val messageQueue: InMemoryMessageQueue,
    private val networkMonitor: NetworkMonitor
) : ChatRepository {

    private val _remoteMessages = MutableStateFlow<List<Message>>(emptyList())

    override val messages: Flow<List<Message>> =
        combine(_remoteMessages, messageQueue.queue) { remote, pending ->
            (pending + remote).sortedByDescending { it.timestamp }
        }

    override val isOnline: Flow<Boolean> = networkMonitor.isOnline

    init {
        CoroutineScope(Dispatchers.IO).launch {
            networkMonitor.isOnline.collect { online ->
                if (online) flushQueue()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            realTimeClient.incomingMessages.collect { serverMsg ->

                messageQueue.remove(serverMsg.id)

                _remoteMessages.value =
                    _remoteMessages.value
                        .filterNot { it.id == serverMsg.id }
                        .let { listOf(serverMsg) + it }
            }
        }
    }

    override suspend fun connect() {
        realTimeClient.connect()
    }

    override suspend fun disconnect() {
        realTimeClient.disconnect()
    }

    override suspend fun sendMessage(content: String, chatId: String) {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val pendingMessage = Message(
            id = id,
            chatId = chatId,
            content = content,
            timestamp = timestamp,
            isSentByUser = true,
            status = MessageStatus.PENDING
        )

        messageQueue.add(pendingMessage)

        if (networkMonitor.isOnline.first()) {
            realTimeClient.emitMessage(
                content = content,
                id = id,
                chatId = chatId
            )
        }
    }

    private suspend fun flushQueue() {
        val queuedMessages = messageQueue.getAll()
        queuedMessages.forEach { msg ->
            realTimeClient.emitMessage(msg.content, msg.id, msg.chatId)

            messageQueue.remove(msg.id)
            val sentMsg = msg.copy(status = MessageStatus.SENT)
            val current = _remoteMessages.value.toMutableList()
            current.add(0, sentMsg)
            _remoteMessages.value = current
        }
    }
}