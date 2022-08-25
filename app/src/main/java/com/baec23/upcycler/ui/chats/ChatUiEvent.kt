package com.baec23.upcycler.ui.chats

sealed class ChatUiEvent {
    object LogoutClicked : ChatUiEvent()
    data class ChatInputTextChanged(val text: String) : ChatUiEvent()
    object ChatMessageAdded : ChatUiEvent()
    object ComposableDestroyed : ChatUiEvent()
}