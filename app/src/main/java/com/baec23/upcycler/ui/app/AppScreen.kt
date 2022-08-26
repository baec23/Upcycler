@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baec23.upcycler.model.BottomNavItem
import com.baec23.upcycler.navigation.Navigation
import com.baec23.upcycler.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun AppScreen(
    viewModel: AppViewModel = hiltViewModel(),
    eventChannel: Channel<AppEvent>,
) {
    val navHostController = viewModel.navHostController
    val currScreen by viewModel.currNavScreen
    val snackbarHostState = remember { SnackbarHostState() }
    val eventsFlow = eventChannel.receiveAsFlow()
    val event = eventsFlow.collectAsState(initial = AppEvent.None).value
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(event) {
        when (event) {
            is AppEvent.NavigateUp -> {
                navHostController.navigateUp()
            }
            is AppEvent.NavigateTo -> {
                navHostController.navigate(event.screen.route)
            }
            is AppEvent.NavigateToAndClearBackstack -> {
                navHostController.popBackStack(navHostController.graph.id, true)
                navHostController.navigate(event.screen.route)
            }
            is AppEvent.NavigateToWithArgs -> {
                navHostController.navigate(event.screen.withArgs(event.args))
            }
            is AppEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            is AppEvent.Logout -> {
                viewModel.logout()
                navHostController.popBackStack(navHostController.graph.id, true)
                navHostController.navigate(Screen.LoginScreen.route)
            }
            else -> {}
        }
        eventChannel.send(AppEvent.None)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!currScreen.hasCustomTopBar) {
                TopBar(
                    modifier = Modifier.height(60.dp),
                    screenName = currScreen.displayName,
                    onLogout = {
                        coroutineScope.launch { eventChannel.send(AppEvent.Logout) }
                    })
            }
        },
        bottomBar = {
            if (!(viewModel.currNavScreen.value == Screen.LoginScreen || viewModel.currNavScreen.value == Screen.SignUpScreen)) {
                BottomNavigationBar(
                    modifier = Modifier
                        .height(50.dp),
                    items = listOf(
                        BottomNavItem(
                            name = "Main",
                            icon = Icons.Default.Home,
                            screen = Screen.MainScreen
                        ),
                        BottomNavItem(
                            name = "My Jobs",
                            icon = Icons.Default.List,
                            screen = Screen.MyJobHistoryScreen
                        ),
                        BottomNavItem(
                            name = "Chats",
                            icon = Icons.Default.Face,
                            screen = Screen.ChatListScreen
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
fun TopBar(
    modifier: Modifier = Modifier,
    screenName: String,
    onLogout: () -> Unit = {}
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(start = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = screenName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = {
            dropdownMenuExpanded = true
        }) {
            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                expanded = dropdownMenuExpanded,
                onDismissRequest = { dropdownMenuExpanded = false }) {
                DropdownMenuContent(modifier = Modifier.height(25.dp)) {
                    dropdownMenuExpanded = false
                    onLogout()
                }
            }
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}

@Composable
fun DropdownMenuContent(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    DropdownMenuItem(
        modifier = modifier.background(MaterialTheme.colorScheme.primary),
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onPrimary,
            leadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
        },
        text = {
            Text("Logout")
        }, onClick = onLogout
    )
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
            val isSelected = item == selectedItem
            NavigationBarItem(
                selected = isSelected,
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
                        Row(
                            modifier = Modifier.height(30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = item.icon, contentDescription = null)
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = item.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Clip,
                                    fontSize = 15.sp
                                )
                            }
                        }

                    }
                },
                onClick = {
                    selectedItem = item
                    onItemClick(item)
                })
        }
    }
}