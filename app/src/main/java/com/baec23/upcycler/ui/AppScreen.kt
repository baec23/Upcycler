package com.baec23.upcycler.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.baec23.upcycler.util.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


@Composable
fun AppScreen(
    eventChannel: Channel<AppEvent>
) {
    val navHostController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    val eventsFlow = eventChannel.receiveAsFlow()
    val event = eventsFlow.collectAsState(initial = AppEvent.None).value
    LaunchedEffect(event) {
        when (event) {
            is AppEvent.NavigateTo -> {
                Log.d(TAG, "AppScreen: Navigating")
                navHostController.navigate(event.screen.route)
            }
            is AppEvent.NavigateToWithArgs -> {
                navHostController.navigate(event.screen.withArgs(event.args))
            }
            is AppEvent.ShowSnackbar -> scaffoldState.snackbarHostState.showSnackbar(event.message)
            else -> {}
        }
        eventChannel.send(AppEvent.None)
    }

    Scaffold(scaffoldState = scaffoldState)
    {
        Column(modifier = Modifier.padding(it)) {
            Navigation(
                navHostController = navHostController
            )
        }
    }
}
