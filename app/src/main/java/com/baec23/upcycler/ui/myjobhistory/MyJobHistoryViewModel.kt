package com.baec23.upcycler.ui.myjobhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyJobHistoryViewModel @Inject constructor(
    userRepository: UserRepository,
    jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {
    val userId = userRepository.currUser?.id
    val myJobList = jobRepository.jobsStateFlow.map {
        it.filter { job -> job.creatorId == userId }
    }

    fun onEvent(event: MyJobHistoryUiEvent) {
        when (event) {
            is MyJobHistoryUiEvent.JobPressed -> {
                viewModelScope.launch {
                    appEventChannel.send(
                        AppEvent.NavigateToWithArgs(
                            screen = Screen.JobDetailsScreen,
                            args = event.job.jobId.toString()
                        )
                    )
                }
            }
        }
    }
}