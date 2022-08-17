package com.baec23.upcycler.ui.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.util.Screen
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.AppEvent
import com.baec23.upcycler.util.InputValidator
import com.baec23.upcycler.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {

    val loginFormState: MutableState<LoginFormState> =
        mutableStateOf(LoginFormState())
    val loginScreenState: MutableState<ScreenState> =
        mutableStateOf(ScreenState.Ready)
    val canLogin: MutableState<Boolean> = mutableStateOf(false)

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.LoginIdChanged -> {
                if (InputValidator.isLoginIdValid(event.loginId))
                    loginFormState.value =
                        loginFormState.value.copy(loginId = event.loginId, loginIdErrorMessage = "")
                else
                    loginFormState.value = loginFormState.value.copy(
                        loginId = event.loginId,
                        loginIdErrorMessage = "Id must be at least 4 characters"
                    )
                updateCanLogIn()
            }
            is LoginUiEvent.PasswordChanged -> {
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
                updateCanLogIn()
            }
            LoginUiEvent.LoginPressed -> tryLogin()
            LoginUiEvent.SignUpPressed -> viewModelScope.launch {
                appEventChannel.send(
                    AppEvent.NavigateTo(Screen.SignUpScreen)
                )
            }
        }
    }

    private fun tryLogin() {
        loginScreenState.value = ScreenState.Busy
        CoroutineScope(Dispatchers.IO).launch {
            val loginId = loginFormState.value.loginId
            val password = loginFormState.value.password
            val result = userRepository.tryLogin(loginId, password)
            when {
                result.isSuccess -> {
                    viewModelScope.launch { appEventChannel.send(AppEvent.NavigateTo(Screen.MainScreen)) }
                    loginScreenState.value = ScreenState.Ready
                }
                result.isFailure -> {
                    clearForm()
                    viewModelScope.launch {
                        appEventChannel.send(AppEvent.ShowSnackbar("Couldn't Log In!"))
                    }
                    loginScreenState.value = ScreenState.Ready
                }
            }
        }
    }

    private fun updateCanLogIn() {
        val formState = loginFormState.value
        canLogin.value = formState.loginId.isNotEmpty() &&
                formState.password.isNotEmpty() &&
                formState.loginIdErrorMessage.isEmpty() &&
                formState.passwordErrorMessage.isEmpty()
    }

    private fun clearForm() {
        loginFormState.value = LoginFormState()
    }
}