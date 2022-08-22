package com.baec23.upcycler.ui.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.model.User
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.DataStoreRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.DSKEY_SAVED_USER_ID
import com.baec23.upcycler.util.InputValidator
import com.baec23.upcycler.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
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
                updateCanLogin()
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
                updateCanLogin()
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
        viewModelScope.launch {
            val loginId = loginFormState.value.loginId
            val password = loginFormState.value.password
            val result = userRepository.tryLogin(loginId, password)
            when {
                result.isSuccess -> {
                    dataStoreRepository.putInt(DSKEY_SAVED_USER_ID, result.getOrDefault(User()).id)
                    appEventChannel.send(
                        AppEvent.NavigateToAndClearBackstack(
                            Screen.LoginScreen,
                            Screen.MainScreen
                        )
                    )
                    loginScreenState.value = ScreenState.Ready
                }
                result.isFailure -> {
                    clearForm()
                    appEventChannel.send(AppEvent.ShowSnackbar("Couldn't Log In!"))
                    loginScreenState.value = ScreenState.Ready
                }
            }
        }
    }

    private fun updateCanLogin() {
        val formState = loginFormState.value
        canLogin.value = formState.loginId.isNotEmpty() &&
                formState.password.isNotEmpty() &&
                formState.loginIdErrorMessage.isEmpty() &&
                formState.passwordErrorMessage.isEmpty()
    }

    private fun clearForm() {
        loginFormState.value = LoginFormState()
        updateCanLogin()
    }
}
