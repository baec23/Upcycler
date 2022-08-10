package com.baec23.upcycler.ui.signup

sealed class SignUpScreenState {
    object WaitingForInput : SignUpScreenState()
    object Busy : SignUpScreenState()
    object SignedUp : SignUpScreenState()
    data class Error(val errorMessage: String) : SignUpScreenState()
}
