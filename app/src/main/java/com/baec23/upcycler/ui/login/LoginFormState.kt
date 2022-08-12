package com.baec23.upcycler.ui.login

data class LoginFormState(
    val loginId: String = "",
    val password: String = "",
    val loginIdErrorMessage: String = "",
    val passwordErrorMessage: String = ""
)