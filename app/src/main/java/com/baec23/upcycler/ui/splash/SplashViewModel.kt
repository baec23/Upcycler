package com.baec23.upcycler.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.util.Screen
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.ui.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            jobRepository.registerJobListListener { result ->
                if (result == "Success") {
                    viewModelScope.launch { appEventChannel.send(AppEvent.NavigateTo(Screen.LoginScreen)) }
                }
            }
        }
    }
}