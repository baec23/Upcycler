package com.baec23.upcycler.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.repository.ChatRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    userRepository: UserRepository,
    private val jobRepository: JobRepository,
    chatRepository: ChatRepository,
) : ViewModel() {
    var currUser = userRepository.currUser
    val chatListStateFlow = chatRepository.chatSessionsStateFlow

    fun onEvent(event: ChatListUiEvent) {
        when (event) {
            is ChatListUiEvent.ChatSessionClicked -> TODO()
        }
    }

    fun getJobImageUrl(jobId: Int) : String{
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
            chatRepository.registerChatSessionListener(currUser!!.id)
        }
    }
}