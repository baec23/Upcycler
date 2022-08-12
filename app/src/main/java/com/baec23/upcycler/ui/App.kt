package com.baec23.upcycler.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController


@Composable
fun App() {
    val eventChannel = AppEventChannel
    val navHostController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val event by eventChannel.eventChannel.collectAsState(initial = AppEvent.None)

    Scaffold(scaffoldState = scaffoldState)
    {
        Navigation(
            navHostController = navHostController,
            appChannel = eventChannel
        )
        LaunchedEffect(event) {
            when (event) {
                is AppEvent.NavigateTo -> navHostController.navigate((event as AppEvent.NavigateTo).route.route)
                is AppEvent.ShowSnackbar ->
                    scaffoldState.snackbarHostState.showSnackbar((event as AppEvent.ShowSnackbar).message)
                AppEvent.None -> {}
            }
        }
    }
}
