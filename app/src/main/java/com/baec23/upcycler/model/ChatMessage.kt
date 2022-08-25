package com.baec23.upcycler.model

data class ChatMessage(
    val sessionId: Int = 0,
    val userId: Int = 0,
    val userDisplayName: String = "",
    val timestamp: Long = 0,
    val message: String = ""
)