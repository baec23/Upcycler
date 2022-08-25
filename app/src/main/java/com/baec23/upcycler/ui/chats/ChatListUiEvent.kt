package com.baec23.upcycler.ui.chats

import com.baec23.upcycler.model.ChatSession

sealed class ChatListUiEvent {
    data class ChatSessionClicked(val chatSession: ChatSession): ChatListUiEvent()
    object ComposableDestroyed : ChatListUiEvent()
}