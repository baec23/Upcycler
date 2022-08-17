package com.baec23.upcycler.ui.login

sealed class LoginUiEvent{
    data class LoginIdChanged(val loginId: String): LoginUiEvent()
    data class PasswordChanged(val password: String): LoginUiEvent()
    object LoginPressed: LoginUiEvent()
    object SignUpPressed: LoginUiEvent()
}
