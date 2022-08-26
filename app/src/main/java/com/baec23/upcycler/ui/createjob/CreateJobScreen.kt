@file:OptIn(ExperimentalMaterial3Api::class)

package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.baec23.upcycler.ui.shared.ProgressSpinner
import com.baec23.upcycler.util.ScreenState

@Composable
fun CreateJobScreen(
    viewModel: CreateJobViewModel = hiltViewModel()
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val jobTitle by viewModel.titleFormState
    val jobDetails by viewModel.detailsFormState
    val addedBitmaps = viewModel.addedImages
    val screenState by viewModel.screenState

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            bitmap.value?.let { bitmap ->
                viewModel.onEvent(CreateJobUiEvent.BitmapAdded(bitmap))
            }
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AddImageButton(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f)
            ) {
                launcher.launch("image/*")
            }
            JobImagesList(addedBitmaps)
        }
        Spacer(modifier = Modifier.height(25.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = jobTitle,
                onValueChange = { viewModel.onEvent(CreateJobUiEvent.TitleChanged(it)) })
            Spacer(modifier = Modifier.height(25.dp))
            DetailsTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                value = jobDetails,
                onValueChange = { viewModel.onEvent(CreateJobUiEvent.DetailsChanged(it)) })
            Spacer(modifier = Modifier.height(25.dp))
            Button(
                enabled = viewModel.canCreate.value,
                onClick = { viewModel.onEvent(CreateJobUiEvent.SubmitPressed) }) {
                Text(text = "Create")
            }
        }
    }

    when (screenState) {
        ScreenState.Busy -> ProgressSpinner()
        ScreenState.Ready -> {}
    }
}

@Composable
fun JobImagesList(
    bitmaps: List<Bitmap>,
) {
    LazyRow(content = {
        items(count = bitmaps.size) {
            AddedImage(
                painter = rememberAsyncImagePainter(bitmaps[it]),
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f)
            )
        }
    })
}

@Composable
fun AddImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ImageCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            contentAlignment = Alignment.Center
        )
        {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Image",
                tint = Color.DarkGray
            )
        }
    }
}

@Composable
fun AddedImage(
    modifier: Modifier = Modifier,
    painter: Painter,
) {
    ImageCard(
        modifier = modifier,
    ) {
        Image(
            painter = painter,
            contentDescription = "Added Image",
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    onClick: () -> Unit = {},
    Content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .padding(5.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Content()
    }
}

@Composable
fun TitleTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier,
        label = {
            Text(text = "Title")
        }
    )
}

@Composable
fun DetailsTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = "Details")
        }
    )
}

@Preview
@Composable
fun TestComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageCard(modifier = Modifier.width(100.dp)) {
            Text(text = "Hello")
            Spacer(modifier = Modifier.height(100.dp))
        }
        ImageCard(modifier = Modifier.width(100.dp)) {
            Text(text = "World")
        }
    }
}