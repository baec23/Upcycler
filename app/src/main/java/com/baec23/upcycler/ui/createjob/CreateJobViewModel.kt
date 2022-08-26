package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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

    private val _screenState: MutableState<ScreenState> =
        mutableStateOf(ScreenState.Ready)
    val screenState: State<ScreenState> = _screenState
    private val _addedImages: MutableList<Bitmap> = mutableStateListOf()
    val addedImages: List<Bitmap> = _addedImages
    private val _titleFormState: MutableState<String> = mutableStateOf("")
    val titleFormState: State<String> = _titleFormState
    private val _detailsFormState: MutableState<String> = mutableStateOf("")
    val detailsFormState: State<String> = _detailsFormState
    private val _canCreate: MutableState<Boolean> = mutableStateOf(false)
    val canCreate: State<Boolean> = _canCreate

    fun onEvent(event: CreateJobUiEvent) {
        when (event) {
            is CreateJobUiEvent.BitmapAdded -> {
                _addedImages.add(event.addedBitmap)
                updateCanCreate()
            }
            is CreateJobUiEvent.TitleChanged -> {
                _titleFormState.value = event.titleText
                updateCanCreate()
            }
            is CreateJobUiEvent.DetailsChanged -> {
                _detailsFormState.value = event.detailsText
                updateCanCreate()
            }
            CreateJobUiEvent.SubmitPressed -> {
                tryCreate()
            }
        }
    }

    private fun tryCreate() {
        _screenState.value = ScreenState.Busy
        val currUserId = userRepository.currUser?.id
        viewModelScope.launch {
            currUserId?.let {
                val result = jobRepository.tryCreateJob(
                    images = _addedImages,
                    jobTitle = _titleFormState.value,
                    jobDetails = _detailsFormState.value,
                    creatorId = it,
                )
                when {
                    result.isSuccess -> {
                        viewModelScope.launch { appEventChannel.send(AppEvent.NavigateTo(Screen.MainScreen)) }
                        _screenState.value = ScreenState.Ready
                    }
                    else -> {
                        viewModelScope.launch { appEventChannel.send(AppEvent.ShowSnackbar("Couldn't Create Job!")) }
                        _screenState.value = ScreenState.Ready
                    }
                }
            }
        }
    }

    private fun updateCanCreate() {
        _canCreate.value =
            _detailsFormState.value.isNotEmpty() && _titleFormState.value.isNotEmpty() && _addedImages.isNotEmpty()
    }
}