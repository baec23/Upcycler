package com.baec23.upcycler.repository

import android.util.Log
import com.baec23.upcycler.model.ChatMessage
import com.baec23.upcycler.model.ChatSession
import com.baec23.upcycler.util.TAG
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityScoped
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    storage: FirebaseStorage
) {
    private val chatSessionReference: CollectionReference = firestore.collection("chats")
    private val chatReference: CollectionReference = firestore.collection("chatMessages")
    private val keyStoreReference: DocumentReference =
        firestore.collection("keys").document("chatSessions")
    private var chatSessionListenerRegistration: ListenerRegistration? = null
    private var chatListenerRegistration: ListenerRegistration? = null

    private val _chatSessionsStateFlow = MutableStateFlow<List<ChatSession>>(emptyList())
    private val _chatMessagesStateFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    private var currChatSessionId: Int? = null

    fun registerChatSessionListener(userId: Int): StateFlow<List<ChatSession>> {
        chatSessionListenerRegistration =
            chatSessionReference.whereArrayContains("participantUserIds", userId)
                .addSnapshotListener { documentSnapshots, error ->
                    if (error == null) {
                        val toReturn: MutableList<ChatSession> = mutableListOf()
                        documentSnapshots?.forEach { document ->
                            toReturn.add(document.toObject(ChatSession::class.java))
                        }
                        _chatSessionsStateFlow.update { toReturn }
                    }
                }
        return _chatSessionsStateFlow.asStateFlow()
    }

    fun cancelChatSessionListenerRegistration() {
        chatSessionListenerRegistration?.let { it.remove() }
            .also { chatSessionListenerRegistration = null }
    }

    fun registerChatListener(sessionId: Int): StateFlow<List<ChatMessage>> {
        Log.d(TAG, "registerChatListener: Registering chat listener for sessionId: $sessionId")
        cancelChatListenerRegistration()
        chatListenerRegistration =
            chatReference.whereEqualTo("sessionId", sessionId)
                .addSnapshotListener { documentSnapshots, error ->
                    Log.d(TAG, "registerChatListener: Snapshot Listener Triggered")
                    if (error == null) {
                        val messages: MutableList<ChatMessage> = mutableListOf()
                        documentSnapshots?.forEach { document ->
                            messages.add(document.toObject(ChatMessage::class.java))
                        }
                        messages.sortBy {
                            it.timestamp
                        }
                        _chatMessagesStateFlow.update { messages }
                    }
                }
        currChatSessionId = sessionId
        return _chatMessagesStateFlow.asStateFlow()
    }

    fun cancelChatListenerRegistration() {
        Log.d(TAG, "cancelChatListenerRegistration: Removing listener")
        chatListenerRegistration?.let { it.remove() }
            .also {
                chatListenerRegistration = null
                currChatSessionId = null
            }
    }

    fun addChatMessage(chatMessage: ChatMessage) {
        chatReference.document().set(chatMessage)
        CoroutineScope(Dispatchers.IO).launch { updateChatSessionRecentMessage(chatMessage = chatMessage) }
    }

    private suspend fun updateChatSessionRecentMessage(chatMessage: ChatMessage) {
        currChatSessionId?.let {
            val docSnap =
                chatSessionReference.whereEqualTo("chatSessionId", currChatSessionId).get()
                    .await().documents[0]
            val docRef = docSnap.reference
            val currChatSession = docSnap.toObject(ChatSession::class.java)
            currChatSession?.let {
                val updatedChatSession = currChatSession.copy(
                    mostRecentMessage = chatMessage.message,
                    mostRecentMessageTimestamp = chatMessage.timestamp
                )
                docRef.set(updatedChatSession)
            }
        }
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
            val newKey = getNewChatSessionKey()
            val newChatSession = ChatSession(
                chatSessionId = newKey,
                jobCreatorUserId = jobCreatorUserId,
                jobCreatorDisplayName = jobCreatorDisplayName,
                workerUserId = currUserId,
                workerDisplayName = currUserDisplayName,
                jobId = jobId,
                jobImageUrl = jobImageUrl,
                participantUserIds = listOf(jobCreatorUserId, currUserId)
            )
            chatSessionReference.document().set(newChatSession).await()
            return Result.success(newKey)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getChatSessionById(sessionId: Int): ChatSession? {
        val toReturn =
            chatSessionReference.whereEqualTo("chatSessionId", sessionId).get().await().documents[0]
        return toReturn.toObject(ChatSession::class.java)
    }

    private suspend fun getNewChatSessionKey(): Int {
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