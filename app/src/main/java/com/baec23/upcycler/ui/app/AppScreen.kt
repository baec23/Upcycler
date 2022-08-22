@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.baec23.upcycler.model.BottomNavItem
import com.baec23.upcycler.navigation.Navigation
import com.baec23.upcycler.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun AppScreen(
    appViewModel: AppViewModel = hiltViewModel(),
    eventChannel: Channel<AppEvent>,
) {
    val navHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val eventsFlow = eventChannel.receiveAsFlow()
    val event = eventsFlow.collectAsState(initial = AppEvent.None).value
    LaunchedEffect(event) {
        when (event) {
            is AppEvent.NavigateUp -> {
                navHostController.navigateUp()
            }
            is AppEvent.NavigateTo -> {
                appViewModel.setCurrNavScreen(event.screen)
                navHostController.navigate(event.screen.route)
            }
            is AppEvent.NavigateToAndClearBackstack -> {
                navHostController.popBackStack(event.currScreen.route, true)
                appViewModel.setCurrNavScreen(event.destinationScreen)
                navHostController.navigate(event.destinationScreen.route)
            }
            is AppEvent.NavigateToWithArgs -> {
                appViewModel.setCurrNavScreen(event.screen)
                navHostController.navigate(event.screen.withArgs(event.args))
            }
            is AppEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            else -> {}
        }
        eventChannel.send(AppEvent.None)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!(appViewModel.currNavScreen.value == Screen.LoginScreen || appViewModel.currNavScreen.value == Screen.SignUpScreen)) {
                TopBar(modifier = Modifier.height(50.dp), onLogout = {
                    appViewModel.logout()
                    navHostController.currentDestination?.route?.let {
                        navHostController.popBackStack(
                            it, true
                        )
                    }
                    navHostController.navigate(Screen.LoginScreen.route)
                })
            }
        },
        bottomBar = {
            if (!(appViewModel.currNavScreen.value == Screen.LoginScreen || appViewModel.currNavScreen.value == Screen.SignUpScreen)) {
                BottomNavigationBar(
                    modifier = Modifier
                        .height(60.dp),
                    items = listOf(
                        BottomNavItem(
                            name = "Main",
                            icon = Icons.Default.Home,
                            screen = Screen.MainScreen
                        ),
                        BottomNavItem(
                            name = "My Job History",
                            icon = Icons.Default.List,
                            screen = Screen.MyJobHistoryScreen
                        ),
                        BottomNavItem(
                            name = "Chats",
                            icon = Icons.Default.Face,
                            screen = Screen.ChatsScreen
                        ),
                    ), onItemClick = {
                        navHostController.navigate(it.screen.route)
                    })
            }
        }
    )
    {
        Column(modifier = Modifier.padding(it)) {
            Navigation(
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    var selectedItem by remember { mutableStateOf(items[0]) }
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
                ),
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = item.icon, contentDescription = null)
                    }
                },
                onClick = {
                    selectedItem = item
                    onItemClick(item)
                })
        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onLogout) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")

        }

    }
}
