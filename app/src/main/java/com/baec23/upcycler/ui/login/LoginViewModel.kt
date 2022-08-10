package com.baec23.upcycler.ui.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.login.LoginScreenState.LoggedOut
import com.baec23.upcycler.util.InputValidator
import com.baec23.upcycler.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val loginFormState: MutableState<LoginFormState> =
        mutableStateOf(LoginFormState())
    val loginScreenState: MutableState<LoginScreenState> =
        mutableStateOf(LoggedOut)
    val canLogIn: MutableState<Boolean> = mutableStateOf(false)

    fun onEvent(event: LoginUiEvent) {
        Log.d(TAG, "LoginViewModel - onEvent: $event.val")
        when (event) {
            is LoginUiEvent.LoginIdChanged -> {
                updateCanLogIn()
                if (InputValidator.isLoginIdValid(event.loginId))
                    loginFormState.value =
                        loginFormState.value.copy(loginId = event.loginId, loginIdErrorMessage = "")
                else
                    loginFormState.value = loginFormState.value.copy(
                        loginId = event.loginId,
                        loginIdErrorMessage = "Id must be at least 4 characters"
                    )
            }
            is LoginUiEvent.PasswordChanged -> {
                updateCanLogIn()
                if (InputValidator.isPasswordValid(event.password))
                    loginFormState.value = loginFormState.value.copy(
                        password = event.password,
                        passwordErrorMessage = ""
                    )
                else
                    loginFormState.value = loginFormState.value.copy(
                        password = event.password,
                        passwordErrorMessage = "Password must be at least 4 characters"
                    )
            }
            is LoginUiEvent.LoginPressed -> tryLogin()
        }
    }

    private fun tryLogin() {
        loginScreenState.value = LoginScreenState.Busy
        CoroutineScope(Dispatchers.IO).launch {
            val loginId = loginFormState.value.loginId
            val password = loginFormState.value.password
            val result = userRepository.tryLogin(loginId, password)
            when {
                result.isSuccess ->
                    loginScreenState.value = LoginScreenState.LoggedIn
                result.isFailure -> {
                    clearForm()
                    loginScreenState.value = LoginScreenState.Error("Couldn't Log In!")
                }
            }
        }
    }

    private fun updateCanLogIn() {
        val formState = loginFormState.value
        canLogIn.value = formState.loginId.isNotEmpty() &&
                formState.password.isNotEmpty() &&
                formState.loginIdErrorMessage.isEmpty() &&
                formState.passwordErrorMessage.isEmpty()
    }

    private fun clearForm(){
        loginFormState.value = LoginFormState()
    }
}