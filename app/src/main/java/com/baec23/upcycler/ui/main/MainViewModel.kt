package com.baec23.upcycler.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val searchFormState: MutableState<String> = mutableStateOf("")
    val jobList: MutableList<Job> = mutableStateListOf()

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.SearchFormChanged -> searchFormState.value = event.searchText
            is MainUiEvent.JobSelected -> {}
        }
    }

    init {
        jobList.clear()
        jobList.addAll(jobRepository.jobList)
    }
}