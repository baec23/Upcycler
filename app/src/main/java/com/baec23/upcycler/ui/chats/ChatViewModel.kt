package com.baec23.upcycler.ui.chats

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.model.ChatMessage
import com.baec23.upcycler.model.ChatSession
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.User
import com.baec23.upcycler.repository.ChatRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _chatInputFormState: MutableState<ChatInputFormState> =
        mutableStateOf(ChatInputFormState())
    val chatInputFormState: State<ChatInputFormState> = _chatInputFormState
    private val _chatScreenState: MutableState<ScreenState> = mutableStateOf(ScreenState.Busy)
    val chatScreenState: State<ScreenState> = _chatScreenState
    lateinit var chatMessages: StateFlow<List<ChatMessage>>

    var currChatSession: ChatSession? = null
    var currJob: Job? = null
    var currUser: User? = null

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.ChatInputTextChanged -> {
                _chatInputFormState.value =
                    _chatInputFormState.value.copy(chatInputText = event.text)
            }
            is ChatUiEvent.ChatMessageAdded -> {
                val toAdd = ChatMessage(
                    sessionId = currChatSession!!.chatSessionId,
                    userId = currUser!!.id,
                    userDisplayName = currUser!!.displayName,
                    timestamp = System.currentTimeMillis(),
                    message = chatInputFormState.value.chatInputText
                )
                chatRepository.addChatMessage(toAdd)
                _chatInputFormState.value = _chatInputFormState.value.copy(chatInputText = "")
            }
            ChatUiEvent.LogoutClicked -> {
                viewModelScope.launch { appEventChannel.send(AppEvent.Logout) }
            }
            ChatUiEvent.ComposableDestroyed -> chatRepository.cancelChatListenerRegistration()
        }
    }

    init {
        _chatScreenState.value = ScreenState.Busy
        val sessionId = savedStateHandle.get<Int>("chatSessionId")
        sessionId?.let {
            viewModelScope.launch {
                currUser = userRepository.currUser
                currChatSession = chatRepository.getChatSessionById(sessionId)
                currChatSession?.let {
                    currJob = jobRepository.getJobById(currChatSession!!.jobId).getOrNull()
                    chatMessages = chatRepository.registerChatListener(currChatSession!!.chatSessionId)
                }
                _chatScreenState.value = ScreenState.Ready
            }
        }
    }
}