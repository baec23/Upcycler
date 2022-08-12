package com.baec23.upcycler.repository

import android.graphics.Bitmap
import android.net.Uri
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.JobStatus
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@ActivityScoped
class JobRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val jobsReference: CollectionReference = firestore.collection("jobs")
    private val keyStoreReference: DocumentReference = firestore.collection("keys").document("jobs")
    private val jobsStorageReference = storage.reference.child("jobs/")
    val jobList: MutableList<Job> = mutableListOf()

    suspend fun tryCreateJob(
        images: List<Bitmap>,
        jobTitle: String,
        jobDetails: String,
        creatorId: Int,
    ): Result<String> {
        val jobId = getNewKey()
        val uriList = uploadBitmaps(jobId, images)
        val jobToAdd = Job(
            jobId = jobId,
            creatorId = creatorId,
            createdTimestamp = System.currentTimeMillis(),
            title = jobTitle,
            details = jobDetails,
            status = JobStatus.OPEN,
            imageUris = uriList
        )
        return try {
            jobsReference.add(jobToAdd).await()
            Result.success("Created Job")
        } catch (e: Exception){
            Result.failure(Exception("Failed to create job"))
        }
    }

    private suspend fun uploadBitmaps(jobId: Int, toUpload: List<Bitmap>): List<String> {

        val toReturn: MutableList<String> = mutableListOf()
        toUpload.forEachIndexed { i, bitmap ->
            val fileName = "job_${jobId}_$i"
            val fileRef = jobsStorageReference.child(fileName)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val downloadUrl = fileRef.putBytes(data).await().storage.downloadUrl.await()
            toReturn.add(downloadUrl.toString())
        }
        return toReturn
    }

    suspend fun loadJobList(): Result<Boolean> {
        val documentSnapshots = jobsReference
            .whereEqualTo("status", JobStatus.OPEN.name)
            .get()
            .await()
            .documents

        documentSnapshots.forEach { document ->
            val jobToAdd = document.toObject(Job::class.java)
            jobToAdd?.let { jobList.add(it) }
        }

        return Result.success(true)
    }

    private suspend fun getNewKey(): Int {
        var toReturn = 0L
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(keyStoreReference)
            toReturn = snapshot.getLong("value")!!
            val newValue = snapshot.getLong("value")!! + 1
            transaction.update(keyStoreReference, "value", newValue)
        }.await()
        return toReturn.toInt()
    }
}