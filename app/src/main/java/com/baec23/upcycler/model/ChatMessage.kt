package com.baec23.upcycler.model

data class ChatMessage(
    val messageId: Long = 0,
    val sessionId: Long = 0,
    val userId: Long = 0,
    val userDisplayName: String = "",
    val timestamp: Long = 0,
    val message: String = "",
    val hasBeenRead: Boolean = false,
)