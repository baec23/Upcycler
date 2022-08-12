package com.baec23.upcycler.ui

import com.baec23.upcycler.Screen

sealed class AppEvent {
    data class NavigateTo(val route: Screen) : AppEvent()
    data class ShowSnackbar(val message: String) : AppEvent()
    object None : AppEvent()
}
