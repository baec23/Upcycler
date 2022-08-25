package com.baec23.upcycler.model

data class ChatSession(
    val chatSessionId: Int = 0,
    val jobCreatorUserId: Int = 0,
    val jobCreatorDisplayName: String = "",
    val workerUserId: Int = 0,
    val workerDisplayName: String = "",
    val jobId: Int = 0,
    val jobImageUrl: String = "",
    var mostRecentMessage: String = "",
    val mostRecentMessageTimestamp: Long = 0,
    val participantUserIds: List<Int> = emptyList()
)