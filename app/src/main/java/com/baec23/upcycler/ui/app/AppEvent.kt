package com.baec23.upcycler.ui.app

import com.baec23.upcycler.navigation.Screen

sealed class AppEvent {
    object NavigateUp : AppEvent()
    data class NavigateTo(val screen: Screen) : AppEvent()
    data class NavigateToAndClearBackstack(val screen: Screen) : AppEvent()
    data class NavigateToWithArgs(val screen: Screen, val args: String) : AppEvent()
    data class ShowSnackbar(val message: String) : AppEvent()
    object Logout: AppEvent()
    object None : AppEvent()
    object UserInteraction: AppEvent()
}
