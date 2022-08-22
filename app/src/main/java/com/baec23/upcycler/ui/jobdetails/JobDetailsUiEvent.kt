package com.baec23.upcycler.ui.jobdetails

import com.baec23.upcycler.model.Job

sealed class JobDetailsUiEvent{
    object AddToFavoritesPressed: JobDetailsUiEvent()
    object ChatPressed: JobDetailsUiEvent()
    data class DeletePressed(val job: Job): JobDetailsUiEvent()
}
