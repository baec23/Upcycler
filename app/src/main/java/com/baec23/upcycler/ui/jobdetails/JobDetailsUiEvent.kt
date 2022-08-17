package com.baec23.upcycler.ui.jobdetails

sealed class JobDetailsUiEvent{
    object AddToFavoritesPressed: JobDetailsUiEvent()
    object ChatPressed: JobDetailsUiEvent()
}
