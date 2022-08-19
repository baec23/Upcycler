package com.baec23.upcycler.repository

import android.graphics.Bitmap
import androidx.datastore.preferences.preferencesDataStore
import com.baec23.upcycler.model.Job
import com.baec23.upcycler.model.JobStatus
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _jobsStateFlow = MutableStateFlow<List<Job>>(emptyList())
    val jobsStateFlow = _jobsStateFlow.asStateFlow()

    suspend fun getJobById(jobId: Int): Result<Job> {
        val result = jobsReference.whereEqualTo("jobId", jobId).get().await().documents
        if(result.size == 1){
            val toReturn = result[0].toObject(Job::class.java)
            if(toReturn != null)
                return Result.success(toReturn)
        }
        return Result.failure(Exception("Failed to load job!"))
    }

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
        } catch (e: Exception) {
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

    fun registerJobListListener(callback: (String) -> Unit) {
        jobsReference.addSnapshotListener { documentSnapshots, error ->
            if (error == null) {
                val toReturn: MutableList<Job> = mutableListOf()
                documentSnapshots?.forEach { document ->
                    toReturn.add(document.toObject(Job::class.java))
                }
                _jobsStateFlow.update { toReturn }
                callback.invoke("Success")
            }
        }
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