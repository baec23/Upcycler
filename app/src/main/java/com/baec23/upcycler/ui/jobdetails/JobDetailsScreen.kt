@file:OptIn(ExperimentalPagerApi::class)

package com.baec23.upcycler.ui.jobdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.User
import com.baec23.upcycler.util.DateConverter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager

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

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(25.dp)
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

@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    imageUris: List<String>
) {
    HorizontalPager(
        modifier = modifier,
        count = imageUris.size
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
            contentScale = ContentScale.FillWidth
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
