package com.baec23.upcycler.ui.signup

sealed class SignUpUiEvent {
    data class LoginIdChanged(val loginId: String): SignUpUiEvent()
    data class DisplayNameChanged(val displayName: String): SignUpUiEvent()
    data class Password1Changed(val password1: String): SignUpUiEvent()
    data class Password2Changed(val password1:String, val password2: String): SignUpUiEvent()
    object SignUpPressed: SignUpUiEvent()
}