package com.baec23.upcycler.ui

import com.baec23.upcycler.util.Screen

sealed class AppEvent {
    data class NavigateTo(val screen: Screen) : AppEvent()
    data class NavigateToWithArgs(val screen: Screen, val args: String) : AppEvent()
    data class ShowSnackbar(val message: String) : AppEvent()
    object None : AppEvent()
}
