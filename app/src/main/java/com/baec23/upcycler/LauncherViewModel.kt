package com.baec23.upcycler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.DataStoreRepository
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.DSKEY_SAVED_USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    init {
        viewModelScope.launch {

            var loadedVal = false
            jobRepository.registerJobListListener {
                if (it == "Success") {
                    loadedVal = true
                    _isLoaded.value = true
                }
            }

            val savedUserId = dataStoreRepository.getInt(DSKEY_SAVED_USER_ID)

            if (savedUserId != null) {
                val loginResult = userRepository.trySavedLogin(savedUserId)
                if (loginResult.isSuccess && loadedVal)
                    appEventChannel.send(
                        AppEvent.NavigateToAndClearBackstack(
                            Screen.MainScreen
                        )
                    )
            }
        }
    }
}