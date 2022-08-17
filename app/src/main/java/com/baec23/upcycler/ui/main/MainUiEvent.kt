package com.baec23.upcycler.ui.main

import com.baec23.upcycler.model.Job

sealed class MainUiEvent {
    data class SearchFormChanged(val searchText: String): MainUiEvent()
    data class JobSelected(val selectedJob: Job): MainUiEvent()
    object AddJobPressed: MainUiEvent()
}