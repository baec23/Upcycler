@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.login

import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baec23.upcycler.R
import com.baec23.upcycler.ui.shared.ProgressSpinner
import com.baec23.upcycler.ui.theme.Shapes
import com.baec23.upcycler.util.ScreenState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val screenState by viewModel.loginScreenState
    val formState by viewModel.loginFormState
    val bannerPainter = painterResource(id = R.drawable.upcycling_banner2)
    val bannerContentDescription = "Upcycler Banner"
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = bannerPainter,
            contentDescription = bannerContentDescription
        )
        Box() {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .onKeyEvent {
                            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
                            {
                                focusManager.moveFocus(FocusDirection.Down)
                                return@onKeyEvent true
                            }
                            return@onKeyEvent false
                        },
                    value = formState.loginId,
                    label = {
                        Text(text = "Login Id")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Login Id"
                        )
                    },
                    singleLine = true,
                    onValueChange = {
                        viewModel.onEvent(LoginUiEvent.LoginIdChanged(it))
                    },
                    isError = formState.loginIdErrorMessage.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextField(
                    value = formState.password,
                    label = {
                        Text(text = "Password")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Login Id"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus(true)

                    }),
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        viewModel.onEvent(LoginUiEvent.PasswordChanged(it))
                    },
                    isError = formState.passwordErrorMessage.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        shape = Shapes.small,
                        onClick = { viewModel.onEvent(LoginUiEvent.LoginPressed) },
                        enabled = viewModel.canLogin.value
                    ) {
                        Text(text = "Login")
                    }
                    Button(
                        shape = Shapes.small,
                        onClick = { viewModel.onEvent(LoginUiEvent.SignUpPressed) },
                    ) {
                        Text(text = "Sign Up")
                    }
                }
            }
        }
    }
    when (screenState) {
        ScreenState.Busy -> ProgressSpinner()
        ScreenState.Ready -> {}
    }
}