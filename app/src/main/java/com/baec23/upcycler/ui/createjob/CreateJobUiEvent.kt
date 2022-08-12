package com.baec23.upcycler.ui.createjob

import android.graphics.Bitmap

sealed class CreateJobUiEvent {
    data class TitleChanged(val titleText: String): CreateJobUiEvent()
    data class DetailsChanged(val detailsText: String): CreateJobUiEvent()
    data class BitmapAdded(val addedBitmap: Bitmap): CreateJobUiEvent()
    object CreateJobPressed: CreateJobUiEvent()
}