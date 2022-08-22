package com.baec23.upcycler.ui.app

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    private val _currNavScreen: MutableState<Screen> = mutableStateOf(Screen.LoginScreen)
    val currNavScreen: State<Screen> = _currNavScreen

    fun setCurrNavScreen(screen: Screen){
        _currNavScreen.value = screen
    }

    fun logout(){
        userRepository.logout()
        viewModelScope.launch { dataStoreRepository.remove(DSKEY_SAVED_USER_ID) }
    }
}