package com.baec23.upcycler.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.model.ChatSession
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.ChatRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    lateinit var chatSessions: StateFlow<List<ChatSession>>

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
            ChatListUiEvent.ComposableDestroyed -> chatRepository.cancelChatSessionListenerRegistration()
        }
    }

    fun getJobImageUrl(jobId: Int): String {
        var job: Job? = null
        viewModelScope.launch {
            job = jobRepository.getJobById(jobId).getOrNull()
        }
        job?.let {
            return job!!.imageUris[0]
        }
        return ""
    }

    init {
        currUser?.let {
            chatSessions = chatRepository.registerChatSessionListener(currUser!!.id)
        }
    }
}