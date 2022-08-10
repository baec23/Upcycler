package com.baec23.upcycler.ui.signup

data class SignUpFormState(
    val loginId: String = "",
    val displayName: String = "",
    val password1: String = "",
    val password2: String = "",

    val loginIdErrorMessage: String = "",
    val displayNameErrorMessage: String = "",
    val password1ErrorMessage: String = "",
    val password2ErrorMessage: String = "",
)