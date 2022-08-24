package com.baec23.upcycler.ui.app

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.DataStoreRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.util.DSKEY_SAVED_USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
    val navHostController: NavHostController
) : ViewModel() {
    private val _currNavScreen: MutableState<Screen> = mutableStateOf(Screen.LoginScreen)
    val currNavScreen: State<Screen> = _currNavScreen

    fun logout() {
        userRepository.logout()
        viewModelScope.launch { dataStoreRepository.remove(DSKEY_SAVED_USER_ID) }
    }

    init {
        viewModelScope.launch {
            navHostController.currentBackStackEntryFlow.collect {
                when (it.destination.route) {
                    "login_screen" -> _currNavScreen.value = Screen.LoginScreen
                    "signup_screen" -> _currNavScreen.value = Screen.SignUpScreen
                    "main_screen" -> _currNavScreen.value = Screen.MainScreen
                    "createjob_screen" -> _currNavScreen.value = Screen.CreateJobScreen
                    "jobdetails_screen/{jobId}" -> _currNavScreen.value = Screen.JobDetailsScreen
                    "myjobhistory_screen" -> _currNavScreen.value = Screen.MyJobHistoryScreen
                    "chats_screen" -> _currNavScreen.value = Screen.ChatListScreen
                }
            }
        }
    }
}