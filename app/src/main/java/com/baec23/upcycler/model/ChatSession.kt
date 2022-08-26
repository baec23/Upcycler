package com.baec23.upcycler.model

data class ChatSession(
    val chatSessionId: Long = 0,
    val jobCreatorUserId: Long = 0,
    val jobCreatorDisplayName: String = "",
    val workerUserId: Long = 0,
    val workerDisplayName: String = "",
    val jobId: Long = 0,
    val jobImageUrl: String = "",
    var mostRecentMessage: String = "",
    val mostRecentMessageTimestamp: Long = 0,
    val participantUserIds: List<Long> = emptyList()
)