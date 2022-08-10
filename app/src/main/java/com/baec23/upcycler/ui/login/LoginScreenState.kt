package com.baec23.upcycler.ui.login

sealed class LoginScreenState {
    object LoggedOut : LoginScreenState()
    object Busy : LoginScreenState()
    object LoggedIn : LoginScreenState()
    data class Error(val errorMessage: String) : LoginScreenState()
}
