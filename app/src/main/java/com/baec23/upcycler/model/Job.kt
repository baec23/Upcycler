package com.baec23.upcycler.model

data class Job(
    val jobId: Int = 0,
    val creatorId: Int = 0,
    val createdTimestamp: Long = 0L,
    val title: String = "",
    val details: String = "",
    val status: JobStatus = JobStatus.OPEN,
    val imageUris: List<String> = emptyList()
)