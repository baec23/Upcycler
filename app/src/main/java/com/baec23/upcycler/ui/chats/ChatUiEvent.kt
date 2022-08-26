package com.baec23.upcycler.ui.chats

import com.baec23.upcycler.model.ChatMessage

sealed class ChatUiEvent {
    object LogoutClicked : ChatUiEvent()
    data class ChatInputTextChanged(val text: String) : ChatUiEvent()
    object ChatMessageAdded : ChatUiEvent()
    data class ChatMessageRead(val chatMessage: ChatMessage) : ChatUiEvent()
    object LeaveChatSessionClicked : ChatUiEvent()
}