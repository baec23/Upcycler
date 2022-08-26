package com.baec23.upcycler.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {

    val searchFormState: MutableState<String> = mutableStateOf("")
    private val _filteredJobList: MutableStateFlow<List<Job>> = MutableStateFlow(emptyList())
    val filteredJobList: StateFlow<List<Job>> = _filteredJobList

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.SearchFormChanged -> {
                searchFormState.value = event.searchText
                viewModelScope.launch {
                    filterJobsBySearch(jobRepository.jobsStateFlow.value)
                }
            }
            is MainUiEvent.JobSelected -> {
                viewModelScope.launch {
                    appEventChannel.send(
                        AppEvent.NavigateToWithArgs(
                            screen = Screen.JobDetailsScreen,
                            args = event.selectedJob.jobId.toString()
                        )
                    )
                }
            }
            MainUiEvent.AddJobPressed -> viewModelScope.launch {
                appEventChannel.send(
                    AppEvent.NavigateTo(
                        Screen.CreateJobScreen
                    )
                )
            }
        }
    }

    private fun filterJobsBySearch(jobList: List<Job>) {
        var filteredJobs = jobList
        if (searchFormState.value.isNotEmpty()) {
            filteredJobs = filteredJobs.filter {
                it.title.toLowerCase(locale = Locale.current).contains(searchFormState.value.toLowerCase(locale = Locale.current))
            }
        }
        _filteredJobList.value = filteredJobs
    }

    init {
        viewModelScope.launch {
            jobRepository.jobsStateFlow.collect{
                filterJobsBySearch(it)
            }
        }
    }
}