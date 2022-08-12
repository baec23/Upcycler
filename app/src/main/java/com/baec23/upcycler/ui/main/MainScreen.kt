@file:OptIn(ExperimentalFoundationApi::class)

package com.baec23.upcycler.ui.main


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baec23.upcycler.R
import com.baec23.upcycler.Screen
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.ui.AppEvent
import com.baec23.upcycler.ui.AppEventChannel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    appChannel: AppEventChannel
) {
    val scaffoldState = rememberScaffoldState()
    val searchFormState by viewModel.searchFormState
    val jobs = viewModel.jobList

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { appChannel.fireEvent(AppEvent.NavigateTo(Screen.CreateJobScreen)) }
            ) { Icon(Icons.Default.AddCircle, "Create Job") }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(20.dp))
                SearchBar(searchFormState = searchFormState, onValueChange = {
                    viewModel.onEvent(MainUiEvent.SearchFormChanged(it))
                })
                Spacer(modifier = Modifier.height(20.dp))
                JobGrid(jobs = jobs)
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
            .padding(15.dp),
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
    jobs: List<Job>
) {
    val wornWallet = painterResource(id = R.drawable.worn_wallet2)

    LazyVerticalGrid(
        cells = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            items(jobs.size) {
                JobImageCard(painter = wornWallet, jobTitle = jobs[it].title)
            }
        })
}

@Composable
fun JobImageCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    jobTitle: String,
    cardWidth: Dp = 200.dp,
    cardHeight: Dp = 200.dp,
    fontFamily: FontFamily = FontFamily.Default,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Box(modifier = Modifier.height(cardHeight))
        {
            //background image
            Image(
                painter = painter,
                contentDescription = jobTitle,
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
                    text = jobTitle,
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