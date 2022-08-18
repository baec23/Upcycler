package com.baec23.upcycler.ui.app

import com.baec23.upcycler.navigation.Screen

sealed class AppEvent {
    data class NavigateTo(val screen: Screen) : AppEvent()
    data class NavigateToWithArgs(val screen: Screen, val args: String) : AppEvent()
    data class ShowSnackbar(val message: String) : AppEvent()
    object None : AppEvent()
}
