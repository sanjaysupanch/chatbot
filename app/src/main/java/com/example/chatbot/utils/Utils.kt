package com.example.chatbot.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun formatTime(time: Long?): String {
        if (time == null) return ""
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time))
    }
}