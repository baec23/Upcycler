package com.baec23.upcycler.ui.createjob

sealed class CreateJobScreenState {
    object WaitingForInput: CreateJobScreenState()
    object Busy: CreateJobScreenState()
    object JobCreated: CreateJobScreenState()
    data class Error(val errorMessage: String): CreateJobScreenState()
}