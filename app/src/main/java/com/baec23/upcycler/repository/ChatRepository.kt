package com.baec23.upcycler.repository

import com.baec23.upcycler.model.ChatSession
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityScoped
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    storage: FirebaseStorage
) {
    private val chatSessionReference: CollectionReference = firestore.collection("chats")
    private val keyStoreReference: DocumentReference =
        firestore.collection("keys").document("chatSessions")
    private var chatSessionListenerRegistration: ListenerRegistration? = null

    private val _chatSessionsStateFlow = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessionsStateFlow = _chatSessionsStateFlow.asStateFlow()

    fun registerChatSessionListener(userId: Int) {
        chatSessionListenerRegistration =
            chatSessionReference.whereEqualTo("jobCreatorUserId", userId)
                .addSnapshotListener { documentSnapshots, error ->
                    if (error == null) {
                        val toReturn: MutableList<ChatSession> = mutableListOf()
                        documentSnapshots?.forEach { document ->
                            toReturn.add(document.toObject(ChatSession::class.java))
                        }
                        _chatSessionsStateFlow.update { toReturn }
                    }
                }
    }

    fun cancelChatSessionListenerRegistration() {
        chatSessionListenerRegistration?.let { it.remove() }
            .also { chatSessionListenerRegistration = null }
    }

    suspend fun getOrCreateChatSession(
        jobCreatorUserId: Int,
        jobCreatorDisplayName: String,
        currUserId: Int,
        currUserDisplayName: String,
        jobId: Int,
        jobImageUrl: String
    ): Result<Int> {
        try {
            val existingChatSession = chatSessionReference
                .whereEqualTo("jobCreatorUserId", jobCreatorUserId)
                .whereEqualTo("workerUserId", currUserId)
                .get()
                .await()
                .documents
            if (existingChatSession.isNotEmpty()) {
                val chatSessionId = existingChatSession[0].getDouble("chatSessionId")?.toInt() ?: -1
                return if (chatSessionId < 0)
                    Result.failure(Exception("chatSessionId error"))
                else
                    Result.success(chatSessionId)
            }
            val newKey = getNewKey()
            val newChatSession = ChatSession(
                chatSessionId = newKey,
                jobCreatorUserId = jobCreatorUserId,
                jobCreatorDisplayName = jobCreatorDisplayName,
                workerUserId = currUserId,
                workerDisplayName = currUserDisplayName,
                jobId = jobId,
                jobImageUrl = jobImageUrl
            )
            chatSessionReference.document().set(newChatSession).await()
            return Result.success(newKey)
        } catch (e: Exception) {
            return Result.failure(e)
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