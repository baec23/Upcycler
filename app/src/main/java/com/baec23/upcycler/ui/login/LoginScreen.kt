package com.baec23.upcycler.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baec23.upcycler.R
import com.baec23.upcycler.ui.shared.ProgressSpinner
import com.baec23.upcycler.ui.signup.SignUpScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
) {
    val screenState by viewModel.loginScreenState
    val formState by viewModel.loginFormState
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val bannerPainter = painterResource(id = R.drawable.upcycling_banner2)
    val bannerContentDescription = "Upcycler Banner"

    Scaffold(scaffoldState = scaffoldState)
    {
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
                        onValueChange = {
                            viewModel.onEvent(LoginUiEvent.LoginIdChanged(it))
                        },
                        isError = formState.loginIdErrorMessage.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                            onClick = { viewModel.onEvent(LoginUiEvent.LoginPressed) },
                            enabled = viewModel.canLogIn.value
                        ) {
                            Text(text = "Login")
                        }
                        Button(
                            onClick = { navController.navigate("signup_screen") },
                        ) {
                            Text(text = "Sign Up")
                        }
                    }
                }
            }
        }
        when (screenState) {
            LoginScreenState.LoggedIn -> LaunchedEffect(Unit) {
                navController.navigate("main_screen")
            }
            LoginScreenState.Busy -> ProgressSpinner()
            is LoginScreenState.Error -> {
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