package com.baec23.upcycler.ui.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baec23.upcycler.Screen
import com.baec23.upcycler.ui.AppEvent
import com.baec23.upcycler.ui.AppEventChannel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    appChannel: AppEventChannel
) {
    val isLoaded by viewModel.isLoaded
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        Text(text = "Hello World")
    }
    when (isLoaded) {
        true -> appChannel.fireEvent(AppEvent.NavigateTo(Screen.LoginScreen))
    }
}