package com.baec23.upcycler.ui.jobdetails

import com.baec23.upcycler.model.Job

sealed class JobDetailsUiEvent{
    object AddToFavoritesPressed: JobDetailsUiEvent()
    object ChatPressed: JobDetailsUiEvent()
    object LogoutPressed: JobDetailsUiEvent()
    data class EditPressed(val job: Job): JobDetailsUiEvent()
    data class DeletePressed(val job: Job): JobDetailsUiEvent()
}
