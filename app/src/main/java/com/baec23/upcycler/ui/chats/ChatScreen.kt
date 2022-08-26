@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.ChatMessage
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.ui.createjob.ImageCard
import com.baec23.upcycler.ui.myjobhistory.HorizontalDividerLine
import com.baec23.upcycler.ui.shared.ProgressSpinner
import com.baec23.upcycler.util.ScreenState

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
) {
    if (viewModel.chatScreenState.value == ScreenState.Busy)
        ProgressSpinner()
    else {
        val focusManager = LocalFocusManager.current
        val chatMessages = viewModel.chatMessages.collectAsState()
        val myDisplayName = viewModel.currUser!!.displayName
        val otherUserDisplayName =
            if (viewModel.currChatSession!!.jobCreatorDisplayName == myDisplayName)
                viewModel.currChatSession!!.workerDisplayName
            else
                viewModel.currChatSession!!.jobCreatorDisplayName

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ChatTopBar(
                    modifier = Modifier.height(60.dp),
                    screenName = otherUserDisplayName,
                    onMenuItemClicked = { clickedMenuItem ->
                        when (clickedMenuItem) {
                            DropdownMenuSelection.Leave -> {
                                viewModel.onEvent(ChatUiEvent.LeaveChatSessionClicked)
                            }
                            DropdownMenuSelection.Logout -> {
                                viewModel.onEvent(ChatUiEvent.LogoutClicked)
                            }
                        }
                    })
            },
            bottomBar = {
                ChatInputSection(
                    modifier = Modifier.height(60.dp),
                    value = viewModel.chatInputFormState.value.chatInputText,
                    onTextChanged = {
                        viewModel.onEvent(ChatUiEvent.ChatInputTextChanged(it))
                    }) {
                    focusManager.clearFocus(true)
                    viewModel.onEvent(ChatUiEvent.ChatMessageAdded)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                JobDetailsSection(
                    modifier = Modifier.height(75.dp),
                    job = viewModel.currJob!!,
                    onClick = {})
                Spacer(modifier = Modifier.height(5.dp))
                ChatMessagesSection(
                    modifier = Modifier.fillMaxWidth(),
                    messages = chatMessages.value,
                    currUserId = viewModel.currUser!!.id,
                    onMessageRead = { message ->
                        viewModel.onEvent(ChatUiEvent.ChatMessageRead(message))
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatTopBar(
    modifier: Modifier = Modifier,
    screenName: String,
    onMenuItemClicked: (DropdownMenuSelection) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
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
                ChatDropdownMenuContent(
                    modifier = Modifier.height(25.dp)
                ) {
                    dropdownMenuExpanded = false
                    onMenuItemClicked(it)
                }
            }
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}

@Composable
private fun ChatDropdownMenuContent(
    modifier: Modifier = Modifier,
    onMenuItemClicked: (DropdownMenuSelection) -> Unit
) {
    DropdownMenuItem(
        modifier = modifier.background(MaterialTheme.colorScheme.primary),
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onPrimary,
            leadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Leave")
        },
        text = {
            Text("Leave")
        }, onClick = { onMenuItemClicked(DropdownMenuSelection.Leave) }
    )
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
        }, onClick = { onMenuItemClicked(DropdownMenuSelection.Logout) }
    )
}

@Composable
private fun JobDetailsSection(
    modifier: Modifier = Modifier,
    job: Job,
    onClick: (Job) -> Unit,
) {
    HorizontalDividerLine()
    Card(modifier = modifier.padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = { onClick(job) }
    ) {
        Row(modifier = modifier) {
            ImageCard(modifier = Modifier.weight(0.2f)) {
                SubcomposeAsyncImage(
                    model = job.imageUris[0],
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                modifier = Modifier.weight(0.8f),
                text = job.title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
    HorizontalDividerLine()
}

@Composable
private fun ChatMessagesSection(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    currUserId: Long,
    onMessageRead: (ChatMessage) -> Unit
) {
    val state = rememberLazyListState()
    LaunchedEffect(messages) {
        if (messages.isNotEmpty())
            state.scrollToItem(messages.size - 1)
    }
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(20.dp)
    ) {
        items(messages.size) { index ->
            ChatMessage(
                message = messages[index],
                isMyMessage = messages[index].userId == currUserId,
                isRead = messages[index].hasBeenRead,
                isLast = index == messages.size - 1,
                onMessageRead = onMessageRead,
            )
            if (index < messages.size - 1) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
private fun ChatMessage(
    message: ChatMessage,
    isMyMessage: Boolean,
    isRead: Boolean,
    isLast: Boolean,
    onMessageRead: (ChatMessage) -> Unit,
) {
    val cardColor =
        if (isMyMessage)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.background
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            if (isMyMessage) {
                Spacer(modifier = Modifier.width(50.dp))
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            Text(text = message.message)
                        }
                    }
                    if (isMyMessage && isLast && isRead)
                        Text(
                            text = "Read",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.End
                        )
                }

            }
            if (!isMyMessage) {
                Spacer(modifier = Modifier.width(50.dp))
            }
        }
        if (!isMyMessage && !isRead)
            onMessageRead(message)
    }
}

@Composable
private fun ChatInputSection(
    modifier: Modifier = Modifier,
    value: String,
    onTextChanged: (String) -> Unit,
    onSubmitClicked: () -> Unit,
) {
    Surface() {
        Row(
            modifier = modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.weight(0.85f),
                value = value,
                colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.background),
                onValueChange = { onTextChanged(it) })
            Spacer(modifier = Modifier.width(5.dp))
            IconButton(
                modifier = Modifier
                    .weight(0.15f)
                    .padding(5.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f, matchHeightConstraintsFirst = false)
                    .clip(MaterialTheme.shapes.medium),
                onClick = onSubmitClicked,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = value.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                )
            }
        }
    }
}

private sealed class DropdownMenuSelection {
    object Leave : DropdownMenuSelection()
    object Logout : DropdownMenuSelection()
}