@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.app

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.baec23.upcycler.model.BottomNavItem
import com.baec23.upcycler.navigation.Navigation
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.util.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


@Composable
fun AppScreen(
    eventChannel: Channel<AppEvent>
) {
    val navHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

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
            is AppEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            else -> {}
        }
        eventChannel.send(AppEvent.None)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier
                    .height(75.dp),
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
        modifier = modifier
            .padding(5.dp)
            .clip(MaterialTheme.shapes.large),
        containerColor = MaterialTheme.colorScheme.onBackground,
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
