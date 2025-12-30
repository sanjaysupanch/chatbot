package com.example.chatbot.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatbot.domain.model.Chat
import com.example.chatbot.presentation.viewModel.ChatViewModel
import com.example.chatbot.ui.theme.ErrorRed
import com.example.chatbot.ui.theme.LightGreen
import com.example.chatbot.ui.theme.TealGreen
import com.example.chatbot.utils.Utils.formatTime

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.selectedChatId == null) {
        ChatListScreen(
            chats = uiState.chats,
            isOnline = uiState.isOnline,
            onChatClick = { viewModel.selectChat(it) }
        )
    } else {
        val activeChat = uiState.chats.find { it.id == uiState.selectedChatId }
        activeChat?.let { chat ->
            ChatDetailScreen(
                chat = chat,
                viewModel = viewModel,
                onBack = { viewModel.closeChat() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<Chat>,
    isOnline: Boolean,
    onChatClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Netomi", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TealGreen),
                    actions = {
                        Icon(
                            if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            null,
                            tint = Color.White
                        )
                    }
                )
                AnimatedVisibility(visible = !isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorRed)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Internet Connection", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(chats) { chat ->
                ChatRow(chat) { onChatClick(chat.id) }
            }
        }
    }
}

@Composable
fun ChatRow(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    formatTime(chat.lastMessageTime),
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) LightGreen else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.lastMessage ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(LightGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}