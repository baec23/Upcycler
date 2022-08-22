package com.baec23.upcycler.ui.jobdetails

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.User
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {

    val jobDetailsScreenState: MutableState<ScreenState> = mutableStateOf(ScreenState.Ready)
    val currJob: MutableState<Job> = mutableStateOf(Job())
    val jobOwner: MutableState<User> = mutableStateOf(User())
    val isMyJob: MutableState<Boolean> = mutableStateOf(false)

    fun onEvent(event: JobDetailsUiEvent) {
        when (event) {
            JobDetailsUiEvent.AddToFavoritesPressed -> {}
            JobDetailsUiEvent.ChatPressed -> {}
            is JobDetailsUiEvent.DeletePressed -> {
                viewModelScope.launch {
                    jobRepository.deleteJob(currJob.value)
                    appEventChannel.send(AppEvent.NavigateUp)
                }
            }
        }
    }

    fun setJobId(jobId: Int) {
        jobDetailsScreenState.value = ScreenState.Busy
        viewModelScope.launch {
            val jobResult = jobRepository.getJobById(jobId)
            currJob.value = jobResult.getOrElse { exception ->
                appEventChannel.send(
                    AppEvent.ShowSnackbar(
                        exception.message ?: "Couldn't load job"
                    )
                )
                Job()
            }
            val userResult = userRepository.getUserById(currJob.value.creatorId)
            jobOwner.value = userResult.getOrElse { exception ->
                appEventChannel.send(
                    AppEvent.ShowSnackbar(
                        exception.message ?: "Couldn't load user"
                    )
                )
                User()
            }
            if(userRepository.currUser?.id == jobOwner.value.id)
                isMyJob.value = true
            jobDetailsScreenState.value = ScreenState.Ready
        }
    }
}