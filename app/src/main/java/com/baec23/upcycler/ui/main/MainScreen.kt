@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.ui.theme.Shapes

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val searchFormState by viewModel.searchFormState
    val jobs by viewModel.filteredJobList.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                shape = Shapes.medium,
                onClick = { viewModel.onEvent(MainUiEvent.AddJobPressed) }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create Job"
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SearchBar(searchFormState = searchFormState, onValueChange = { searchString ->
                    viewModel.onEvent(MainUiEvent.SearchFormChanged(searchString))
                })
                JobGrid(jobs = jobs) { job ->
                    viewModel.onEvent(MainUiEvent.JobSelected(job))
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchFormState: String,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = searchFormState,
        label = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        onValueChange = onValueChange
    )
}

@Composable
fun JobGrid(
    jobs: List<Job>,
    onClick: (Job) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            items(jobs.size) {
                JobImageCard(job = jobs[it], onClick = onClick)
            }
        })
}

@Composable
fun JobImageCard(
    modifier: Modifier = Modifier,
    job: Job,
    cardWidth: Dp = 200.dp,
    cardHeight: Dp = 200.dp,
    fontFamily: FontFamily = FontFamily.Default,
    onClick: (Job) -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            onClick(job)
        }
    ) {
        Box(modifier = Modifier.height(cardHeight))
        {
            SubcomposeAsyncImage(
                model = if (job.imageUris.isNotEmpty()) job.imageUris[0] else "https://firebasestorage.googleapis.com/v0/b/upcycler-c570d.appspot.com/o/jobs%2Fjob_3_0?alt=media&token=f41a2696-ecfa-478c-9105-29c1028c6561",
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = job.title,
                contentScale = ContentScale.Crop
            )
            //gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 300f
                        )
                    )
            )
            //text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            )
            {
                Text(
                    text = job.title,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = fontFamily
                    )
                )
            }
        }
    }
}