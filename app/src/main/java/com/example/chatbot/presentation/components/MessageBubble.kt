package com.example.chatbot.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.domain.model.Message
import com.example.chatbot.domain.model.MessageStatus
import com.example.chatbot.ui.theme.BubbleGreen
import com.example.chatbot.ui.theme.ErrorRed
import com.example.chatbot.utils.Utils.formatTime

@Composable
fun MessageBubble(message: Message) {
    val isMe = message.isSentByUser
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMe) BubbleGreen else Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(message.content, fontSize = 16.sp, color = Color.Black)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formatTime(message.timestamp), fontSize = 11.sp, color = Color.Gray)

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))

                        when (message.status) {
                            MessageStatus.PENDING -> Icon(Icons.Default.AccessTime, "Queued", tint = Color.Gray, modifier = Modifier.size(12.dp))
                            MessageStatus.SENT ->  {
                                Icon(Icons.Default.Done, "Sent", tint = Color.Gray, modifier = Modifier.size(14.dp))
                            }
                            MessageStatus.FAILED -> Icon(Icons.Default.Error, "Failed", tint = ErrorRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Pending Message", showBackground = true)
@Composable
fun MessageBubblePendingPreview() {
    MessageBubble(
        message = Message(
            id = "3",
            chatId = "chat_1",
            content = "Sending",
            timestamp = System.currentTimeMillis(),
            isSentByUser = true,
            status = MessageStatus.PENDING
        )
    )
}