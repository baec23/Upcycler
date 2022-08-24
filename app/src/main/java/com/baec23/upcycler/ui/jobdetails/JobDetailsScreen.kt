@file:OptIn(
    ExperimentalPagerApi::class, ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class
)

package com.baec23.upcycler.ui.jobdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.User
import com.baec23.upcycler.ui.myjobhistory.HorizontalDividerLine
import com.baec23.upcycler.ui.theme.Shapes
import com.baec23.upcycler.util.DateConverter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@Composable
fun JobDetailsScreen(
    viewModel: JobDetailsViewModel = hiltViewModel(),
    jobId: Int
) {
    LaunchedEffect(true) {
        viewModel.setJobId(jobId)
    }

    val job by viewModel.currJob
    val jobOwner by viewModel.jobOwner
    val isMyJob by viewModel.isMyJob
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            JobDetailsTopBar(
                modifier = Modifier.height(60.dp),
                isMyJob = isMyJob,
                onMenuItemClicked = { clickedMenuItem ->
                    when (clickedMenuItem) {
                        DropdownMenuSelection.Edit -> {
                            viewModel.onEvent(JobDetailsUiEvent.EditPressed(job))
                        }
                        DropdownMenuSelection.Delete -> {
                            viewModel.onEvent(JobDetailsUiEvent.DeletePressed(job))
                        }
                        DropdownMenuSelection.Logout -> {
                            viewModel.onEvent(JobDetailsUiEvent.LogoutPressed)
                        }
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                shape = Shapes.medium,
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Start Chat"
                )
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Top
        ) {
            ImagePager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                imageUris = job.imageUris
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserDetailsBar(modifier = Modifier.fillMaxWidth(), user = jobOwner)
            Spacer(modifier = Modifier.height(20.dp))
            JobInformationSection(modifier = Modifier.fillMaxWidth(), job = job)
        }
    }

}

@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    imageUris: List<String>
) {
    val pagerState: PagerState = rememberPagerState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = modifier,
            count = imageUris.size,
            state = pagerState,
        ) { page ->
            SubcomposeAsyncImage(
                model = imageUris[page],
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = "Image",
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        DotsIndicator(
            totalDots = pagerState.pageCount,
            selectedIndex = pagerState.currentPage,
            selectedColor = MaterialTheme.colorScheme.inverseSurface,
            unSelectedColor = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
fun UserDetailsBar(
    modifier: Modifier = Modifier,
    user: User
) {
    val userDisplayName = user.displayName
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
        ) {
            Row(
                modifier = modifier.padding(10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Text(
                        text = userDisplayName,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "나는 뭐를 하는 사람이다",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun JobInformationSection(
    modifier: Modifier = Modifier,
    job: Job
) {
    Column(modifier = modifier) {
        Text(
            text = job.title,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = DateConverter.convertTimestampToDate(job.createdTimestamp),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = job.details)
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {
    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}


@Composable
fun JobDetailsTopBar(
    modifier: Modifier = Modifier,
    screenName: String = "Job Details",
    isMyJob: Boolean,
    onMenuItemClicked: (DropdownMenuSelection) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxSize()
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
                JobDetailsDropdownMenuContent(
                    modifier = Modifier.height(25.dp),
                    isMyJob = isMyJob
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
fun JobDetailsDropdownMenuContent(
    modifier: Modifier = Modifier,
    isMyJob: Boolean,
    onMenuItemClicked: (DropdownMenuSelection) -> Unit
) {
    if (isMyJob) {
        DropdownMenuItem(
            modifier = modifier.background(MaterialTheme.colorScheme.primary),
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                leadingIconColor = MaterialTheme.colorScheme.onPrimary
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            },
            text = {
                Text("Edit")
            }, onClick = { onMenuItemClicked(DropdownMenuSelection.Edit) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDividerLine(width = 2.dp)
        Spacer(modifier = Modifier.height(10.dp))
        DropdownMenuItem(
            modifier = modifier.background(MaterialTheme.colorScheme.primary),
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                leadingIconColor = MaterialTheme.colorScheme.onPrimary
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            },
            text = {
                Text("Delete")
            }, onClick = {
                onMenuItemClicked(DropdownMenuSelection.Delete)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDividerLine(width = 2.dp)
        Spacer(modifier = Modifier.height(10.dp))
    }
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

sealed class DropdownMenuSelection {
    object Edit : DropdownMenuSelection()
    object Delete : DropdownMenuSelection()
    object Logout : DropdownMenuSelection()
}