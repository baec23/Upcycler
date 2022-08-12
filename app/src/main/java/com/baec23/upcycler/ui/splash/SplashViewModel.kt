package com.baec23.upcycler.ui.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.baec23.upcycler.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    val isLoaded: MutableState<Boolean> = mutableStateOf(false)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val result = jobRepository.loadJobList()
            when{
                result.isSuccess ->
                    isLoaded.value = true
            }
        }
    }
}