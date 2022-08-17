package com.baec23.upcycler.ui.signup

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
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {
    val signUpFormState: MutableState<SignUpFormState> =
        mutableStateOf(SignUpFormState())
    val screenState: MutableState<ScreenState> =
        mutableStateOf(ScreenState.Ready)
    val canSignUp: MutableState<Boolean> = mutableStateOf(false)

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.LoginIdChanged -> {
                if (event.loginId.isEmpty() || InputValidator.isLoginIdValid(event.loginId))
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            loginId = event.loginId,
                            loginIdErrorMessage = ""
                        )
                else
                    signUpFormState.value = signUpFormState.value.copy(
                        loginId = event.loginId,
                        loginIdErrorMessage = "Id must be at least 4 characters"
                    )
                updateCanSignUp()
            }
            is SignUpUiEvent.DisplayNameChanged -> {
                if (event.displayName.isEmpty() || InputValidator.isDisplayNameValid(event.displayName))
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            displayName = event.displayName,
                            displayNameErrorMessage = ""
                        )
                else
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            displayName = event.displayName,
                            displayNameErrorMessage = "Display name must be at least 4 characters"
                        )
                updateCanSignUp()
            }
            is SignUpUiEvent.Password1Changed -> {
                if (event.password1.isEmpty() || InputValidator.isPasswordValid(event.password1))
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            password1 = event.password1,
                            password1ErrorMessage = ""
                        )
                else
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            password1 = event.password1,
                            password1ErrorMessage = "Password must be at least 4 characters"
                        )
                updateCanSignUp()
            }
            is SignUpUiEvent.Password2Changed -> {
                if (event.password2.isEmpty() ||
                    (InputValidator.isPasswordValid(event.password2) && InputValidator.isPassword2Valid(
                        event.password1,
                        event.password2
                    ))
                )
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            password2 = event.password2,
                            password2ErrorMessage = ""
                        )
                else
                    signUpFormState.value =
                        signUpFormState.value.copy(
                            password2 = event.password2,
                            password2ErrorMessage = "Passwords must match"
                        )
                updateCanSignUp()
            }
            SignUpUiEvent.SignUpPressed -> trySignUp()
        }
    }

    private fun trySignUp() {
        screenState.value = ScreenState.Busy
        CoroutineScope(Dispatchers.IO).launch {
            val loginId = signUpFormState.value.loginId
            val password = signUpFormState.value.password1
            val displayName = signUpFormState.value.displayName
            val result = userRepository.trySignup(loginId, password, displayName)
            when {
                result.isSuccess ->
                    viewModelScope.launch { appEventChannel.send(AppEvent.NavigateTo(Screen.LoginScreen)) }
                result.isFailure -> {
                    clearForm()
                    val e = result.exceptionOrNull()
                    var errorMessage = "Couldn't Sign Up!"
                    if (e != null)
                        errorMessage = e.message!!
                    viewModelScope.launch { appEventChannel.send(AppEvent.ShowSnackbar(errorMessage)) }
                }
            }
        }
    }

    private fun updateCanSignUp() {
        val formState = signUpFormState.value
        canSignUp.value = formState.loginId.isNotEmpty() &&
                formState.displayName.isNotEmpty() &&
                formState.password1.isNotEmpty() &&
                formState.password2.isNotEmpty() &&
                formState.displayNameErrorMessage.isEmpty() &&
                formState.loginIdErrorMessage.isEmpty() &&
                formState.password1ErrorMessage.isEmpty() &&
                formState.password2ErrorMessage.isEmpty()
    }

    private fun clearForm() {
        signUpFormState.value = SignUpFormState()
        canSignUp.value = false

    }
}