package com.baec23.upcycler.model

data class Job(
    val jobId: Int,
    val userId: Int,
    val createdTimestamp: Long,
    val description: String,
    val status: JobStatus,
    val imageUrls: List<String>
)
