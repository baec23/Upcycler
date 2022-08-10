package com.baec23.upcycler.util

object InputValidator {
    fun isLoginIdValid(loginId: String): Boolean {
        return loginId.length >= 4
    }

    fun isDisplayNameValid(displayName: String): Boolean {
        return displayName.length >= 4
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 4
    }

    fun isPassword2Valid(password1: String, password2: String): Boolean {
        return password1 == password2
    }
}