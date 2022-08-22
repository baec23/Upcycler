package com.baec23.upcycler.ui.myjobhistory

import com.baec23.upcycler.model.Job

sealed class MyJobHistoryUiEvent {
    data class JobPressed(val job: Job): MyJobHistoryUiEvent()
}