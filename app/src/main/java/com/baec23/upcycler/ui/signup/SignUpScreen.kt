package com.baec23.upcycler.ui.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baec23.upcycler.ui.shared.ProgressSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController
) {
    val screenState by viewModel.signUpScreenState
    val formState by viewModel.signUpFormState
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 50.dp)
        )
        {
            Box {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = formState.loginId,
                        label = {
                            Text(text = "Login Id")
                        },
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.LoginIdChanged(it))
                        },
                        isError = formState.loginIdErrorMessage.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = formState.displayName,
                        label = {
                            Text(text = "Display Name")
                        },
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.DisplayNameChanged(it))
                        },
                        isError = formState.displayNameErrorMessage.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = formState.password1,
                        label = {
                            Text(text = "Password")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.Password1Changed(it))
                        },
                        isError = formState.password1ErrorMessage.isNotEmpty()
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = formState.password2,
                        label = {
                            Text(text = "Password Again")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = {
                            viewModel.onEvent(
                                SignUpUiEvent.Password2Changed(
                                    formState.password1,
                                    it
                                )
                            )
                        },
                        isError = formState.password2ErrorMessage.isNotEmpty()
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.onEvent(SignUpUiEvent.SignUpPressed) },
                            enabled = viewModel.canSignUp.value
                        ) {
                            Text(text = "Sign Up")
                        }
                    }
                }
            }
        }
        when (screenState) {
            SignUpScreenState.SignedUp ->
                LaunchedEffect(Unit) {
                    navController.navigate("login_screen")
                }
            SignUpScreenState.Busy -> ProgressSpinner()
            is SignUpScreenState.Error -> {
                val errorMessage = (screenState as SignUpScreenState.Error).errorMessage
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                    }
                }
            }
            else -> {}
        }
    }
}
