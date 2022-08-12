package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.baec23.upcycler.repository.JobRepository
import com.baec23.upcycler.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val screenState: MutableState<CreateJobScreenState> =
        mutableStateOf(CreateJobScreenState.WaitingForInput)
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
            CreateJobUiEvent.CreateJobPressed -> {
                tryCreate()
            }
        }
    }

    private fun tryCreate() {
        screenState.value = CreateJobScreenState.Busy
        val currUserId = userRepository.currUser?.id
        coroutineScope.launch {
            currUserId?.let {
                val result = jobRepository.tryCreateJob(
                    images = addedImages,
                    jobTitle = titleFormState.value,
                    jobDetails = detailsFormState.value,
                    creatorId = it,
                )
                when {
                    result.isSuccess -> {
                        screenState.value = CreateJobScreenState.JobCreated
                    }
                    else -> {
                        screenState.value =
                            CreateJobScreenState.Error(result.exceptionOrNull()?.message.toString())
                    }
                }
            }
        }
    }
}