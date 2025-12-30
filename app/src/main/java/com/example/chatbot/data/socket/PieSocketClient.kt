package com.example.chatbot.data.socket

import android.util.Log
import com.example.chatbot.BuildConfig
import com.example.chatbot.domain.model.Message
import com.example.chatbot.domain.model.MessageStatus
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PieSocketClient @Inject constructor() : RealTimeClient {

    private val CLUSTER_ID = "s15617.blr1"
    private val URL = "wss://$CLUSTER_ID.piesocket.com/v3/1?api_key=${BuildConfig.PIESOCKET_API_KEY}&notify_self=1"

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val _connectionState = MutableStateFlow(false)
    override val connectionState: Flow<Boolean> = _connectionState

    private var messageChannel: SendChannel<Message>? = null
    override val incomingMessages: Flow<Message> = callbackFlow {
        messageChannel = this
        awaitClose { messageChannel = null }
    }

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("xxxxxxxxxxxxxxxxxxxxxxxxx "+ URL)
            Log.d("PieSocketClient", "WebSocket connected successfully")
            _connectionState.value = true
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("PieSocketClient", "Message received: $text")
            try {
                val json = JSONObject(text)

                if (json.has("content") || json.has("message")) {
                    val message = Message(
                        id = json.optString("id", System.currentTimeMillis().toString()),
                        chatId = json.optString(
                            "chatId",
                            "1"
                        ),
                        content = json.optString("content", json.optString("message", "")),
                        timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                        isSentByUser = json.optString("sender", "Bot")
                            .equals("User", ignoreCase = true),
                        status = MessageStatus.SENT
                    )
                    messageChannel?.trySend(message)
                }
            } catch (e: Exception) {
                Log.e("PieSocketClient", "Error parsing message: $text", e)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("PieSocketClient", "Binary message received: ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("PieSocketClient", "WebSocket closing: $code - $reason")
            webSocket.close(1000, null)
            _connectionState.value = false
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("PieSocketClient", "WebSocket closed: $code - $reason")
            _connectionState.value = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("PieSocketClient", "WebSocket error", t)
            _connectionState.value = false
        }
    }

    override suspend fun connect() {
        // Clean up existing connection if any
        if (webSocket != null) {
            Log.d("PieSocketClient", "Cleaning up existing connection")
            webSocket?.close(1000, "Reconnecting")
            webSocket = null
        }

        Log.d("PieSocketClient", "Attempting to connect to: $URL")
        try {
            val request = Request.Builder()
                .url(URL)
                .build()

            webSocket = client.newWebSocket(request, webSocketListener)
            Log.d("PieSocketClient", "Connection initiated")
        } catch (e: Exception) {
            Log.e("PieSocketClient", "Error connecting", e)
            _connectionState.value = false
        }
    }

    override suspend fun disconnect() {
        Log.d("PieSocketClient", "Disconnecting...")
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        _connectionState.value = false
    }

    override suspend fun emitMessage(content: String, id: String, chatId: String) {
        if (!_connectionState.value || webSocket == null) {
            Log.w("PieSocketClient", "Cannot send message: not connected")
            return
        }

        try {
            val json = JSONObject().apply {
                put("content", content)
                put("id", id)
                put("chatId", chatId)
                put("sender", "User")
                put("timestamp", System.currentTimeMillis())
            }
            val message = json.toString()
            Log.d("PieSocketClient", "Sending message: $message")

            val sent = webSocket?.send(message)
            if (sent == false) {
                Log.e("PieSocketClient", "Failed to send message")
            }
        } catch (e: Exception) {
            Log.e("PieSocketClient", "Error sending message", e)
        }
    }
}
