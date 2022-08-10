package com.baec23.upcycler.ui.main


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baec23.upcycler.R
import com.baec23.upcycler.ui.JobImageCard

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = "",
                label = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                onValueChange = {
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            JobGrid()
        }
    }
    //List
}

@Composable
fun JobGrid() {

    //TEMP
    val wornWallet = painterResource(id = R.drawable.worn_wallet2)

    LazyColumn {
        items(count = 1)
        {
            JobCardRow()
        }
    }
}

@Composable
fun JobCardRow() {
    Column() {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            JobImageCard(painter = painterResource(id = R.drawable.worn_wallet2), jobTitle = "TEST")
            Spacer(modifier = Modifier.width(10.dp))
            JobImageCard(
                painter = painterResource(id = R.drawable.upcycling_banner),
                jobTitle = "TEST2"
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }

}