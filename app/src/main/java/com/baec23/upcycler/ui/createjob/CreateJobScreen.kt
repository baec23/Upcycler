package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baec23.upcycler.Screen
import com.baec23.upcycler.ui.AppEvent
import com.baec23.upcycler.ui.AppEventChannel
import com.baec23.upcycler.ui.shared.ProgressSpinner

@Composable
fun CreateJobScreen(
    viewModel: CreateJobViewModel = hiltViewModel(),
    appChannel: AppEventChannel
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
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        JobImagesList(addedBitmaps)
        AddImageButton { launcher.launch("image/*") }
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            TitleTextField(
                value = jobTitle,
                onValueChange = { viewModel.onEvent(CreateJobUiEvent.TitleChanged(it)) })
            DetailsTextField(
                value = jobDetails,
                onValueChange = { viewModel.onEvent(CreateJobUiEvent.DetailsChanged(it)) })
            Button(onClick = { viewModel.onEvent(CreateJobUiEvent.CreateJobPressed) }) {
                Text(text = "Create")
            }
        }
    }

    when (screenState) {
        CreateJobScreenState.Busy -> ProgressSpinner()
        is CreateJobScreenState.Error -> appChannel.fireEvent(AppEvent.ShowSnackbar((screenState as CreateJobScreenState.Error).errorMessage))
        CreateJobScreenState.JobCreated -> appChannel.fireEvent(AppEvent.NavigateTo(Screen.MainScreen))
        CreateJobScreenState.WaitingForInput -> {}
    }
}

@Composable
fun JobImagesList(
    bitmaps: List<Bitmap>,
) {
    LazyRow(content = {
        items(count = bitmaps.size) {
            Image(
                bitmap = bitmaps[it].asImageBitmap(),
                contentDescription = "Image_$it",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(200.dp)
            )
        }
    })
}

@Composable
fun AddImageButton(
    onClick: () -> Unit
) {
    Button(onClick = {
        onClick()
    }) {
        Text(text = "Add Image from Gallery")
    }
}

@Composable
fun TitleTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DetailsTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxSize()
    )
}
