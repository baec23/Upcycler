package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baec23.upcycler.navigation.Screen
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val appEventChannel: Channel<AppEvent>
) : ViewModel() {
    val screenState: MutableState<ScreenState> =
        mutableStateOf(ScreenState.Ready)
    val addedImages: MutableList<Bitmap> = mutableStateListOf()
    val titleFormState: MutableState<String> = mutableStateOf("")
    val detailsFormState: MutableState<String> = mutableStateOf("")

    fun onEvent(event: CreateJobUiEvent) {
        when (event) {
            is CreateJobUiEvent.BitmapAdded -> {
                addedImages.add(event.addedBitmap)
            }
            is CreateJobUiEvent.TitleChanged -> {
                titleFormState.value = event.titleText
            }
            is CreateJobUiEvent.DetailsChanged -> {
                detailsFormState.value = event.detailsText
            }
            CreateJobUiEvent.SubmitPressed -> {
                tryCreate()
            }
        }
    }

    private fun tryCreate() {
        screenState.value = ScreenState.Busy
        val currUserId = userRepository.currUser?.id
        viewModelScope.launch {
            currUserId?.let {
                val result = jobRepository.tryCreateJob(
                    images = addedImages,
                    jobTitle = titleFormState.value,
                    jobDetails = detailsFormState.value,
                    creatorId = it,
                )
                when {
                    result.isSuccess -> {
                        viewModelScope.launch { appEventChannel.send(AppEvent.NavigateTo(Screen.MainScreen)) }
                        screenState.value = ScreenState.Ready
                    }
                    else -> {
                        viewModelScope.launch { appEventChannel.send(AppEvent.ShowSnackbar("Couldn't Create Job!")) }
                        screenState.value = ScreenState.Ready
                    }
                }
            }
        }
    }
}