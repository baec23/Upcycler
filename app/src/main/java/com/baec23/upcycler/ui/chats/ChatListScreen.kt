@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.chats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.ChatSession
import com.baec23.upcycler.ui.createjob.ImageCard
import com.baec23.upcycler.ui.myjobhistory.HorizontalDividerLine
import com.baec23.upcycler.util.DateConverter.convertTimestampToDate

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val currUserId = viewModel.currUser!!.id
    val sessionList by viewModel.chatSessions.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = sessionList.size) { index ->
            ChatListItem(
                modifier = Modifier.height(100.dp),
                chatSession = sessionList[index],
                currUserId = currUserId,
                onClick = { viewModel.onEvent(ChatListUiEvent.ChatSessionClicked(sessionList[index])) }
            )
        }
    }
}

@Composable
fun ChatListItem(
    modifier: Modifier = Modifier,
    chatSession: ChatSession,
    currUserId: Int,
    onClick: (ChatSession) -> Unit
) {
    HorizontalDividerLine()
    Spacer(modifier = Modifier.height(5.dp))
    Card(
        modifier = modifier,
        onClick = { onClick(chatSession) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row() {
            Icon(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxSize()
                    .padding(10.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = if (chatSession.jobCreatorUserId == currUserId) chatSession.workerDisplayName else chatSession.jobCreatorDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = convertTimestampToDate(chatSession.mostRecentMessageTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = chatSession.mostRecentMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            ImageCard(modifier = Modifier.weight(0.25f)) {
                SubcomposeAsyncImage(
                    model = chatSession.jobImageUrl,
                    contentDescription = null,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
    HorizontalDividerLine()
}

@Preview
@Composable
fun ChatSessionPreview() {
    val chatSession = ChatSession(
        jobCreatorUserId = 1,
        jobCreatorDisplayName = "Test User 1",
        workerUserId = 2,
        workerDisplayName = "Test User 2",
        jobId = 1,
        jobImageUrl = "https://firebasestorage.googleapis.com/v0/b/upcycler-c570d.appspot.com/o/jobs%2Fjob_11_0?alt=media&token=bf747a5e-1b23-4589-bbc4-1bf0da5de8b8",
        mostRecentMessage = "This is a test recent chat message",
        mostRecentMessageTimestamp = 1660712928898
    )
    ChatListItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        currUserId = 2,
        chatSession = chatSession,
        onClick = {})
}