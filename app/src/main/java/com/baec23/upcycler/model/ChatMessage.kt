package com.baec23.upcycler.model

data class ChatMessage(
    val messageId: Int,
    val userId: Int,
    val userDisplayName: String,
    val timestamp: Long,
    val message: String
)