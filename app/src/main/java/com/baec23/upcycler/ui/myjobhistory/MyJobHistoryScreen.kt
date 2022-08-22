@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.myjobhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.util.DateConverter.convertTimestampToDate

@Composable
fun MyJobHistoryScreen(
    viewModel: MyJobHistoryViewModel = hiltViewModel()
) {
    val myJobList by viewModel.myJobList.collectAsState(initial = null)

    if (myJobList != null) {
        LazyColumn(
            contentPadding = PaddingValues(10.dp)
        ) {
            items(myJobList!!.size) { index ->
                JobEntry(
                    modifier = Modifier.height(100.dp),
                    job = myJobList!![index],
                    onClick = {
                        viewModel.onEvent(MyJobHistoryUiEvent.JobPressed(it))
                    }
                )
                if (index < myJobList!!.size - 1)
                    Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun JobEntry(
    modifier: Modifier = Modifier,
    job: Job,
    onClick: (Job) -> Unit = {},
) {
    Card(
        onClick = { onClick(job) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        HorizontalDividerLine()
        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = modifier.fillMaxWidth()) {
            SubcomposeAsyncImage(
                modifier = Modifier.weight(0.4f),
                model = job.imageUris[0],
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = "Image",
                contentScale = ContentScale.FillWidth
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(10.dp)
            ) {
                JobTextDetails(
                    titleText = job.title,
                    timeText = convertTimestampToDate(job.createdTimestamp)
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDividerLine()
    }
}

@Composable
fun JobTextDetails(
    titleText: String,
    timeText: String,
) {
    Text(
        text = titleText,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = timeText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun HorizontalDividerLine(
    width: Dp = 1.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.inverseOnSurface
) {
    Box(
        modifier = Modifier
            .height(width)
            .fillMaxWidth()
            .background(backgroundColor)
    )
}