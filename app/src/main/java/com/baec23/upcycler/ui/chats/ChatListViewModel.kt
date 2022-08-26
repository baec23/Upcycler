package com.baec23.upcycler.ui.chats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.model.ChatSession
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.ChatRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val chatRepository: ChatRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {
    var currUser = userRepository.currUser
    private val _chatSessions: MutableStateFlow<List<ChatSession>> = MutableStateFlow(emptyList())
    val chatSessions: StateFlow<List<ChatSession>> = _chatSessions

    fun onEvent(event: ChatListUiEvent) {
        when (event) {
            is ChatListUiEvent.ChatSessionClicked -> {
                viewModelScope.launch {
                    appEventChannel.send(
                        AppEvent.NavigateToWithArgs(
                            Screen.ChatScreen,
                            event.chatSession.chatSessionId.toString()
                        )
                    )
                }
            }
        }
    }

    init {
        Log.d(TAG, "ChatListViewModel: INIT! - $this")
        currUser?.let {
            viewModelScope.launch {
                chatRepository.registerChatSessionListener(currUser!!.id).collect {
                    Log.d(TAG, "ChatListViewModel: LISTENER CALLBACK! - ${this@ChatListViewModel}")
                    _chatSessions.value = it
                }
            }
        }
    }
}