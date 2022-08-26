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
    private val chatSessionKeystoreReference: DocumentReference =
        firestore.collection("keys").document("chatSessions")
    private val chatMessageKeystoreReference: DocumentReference =
        firestore.collection("keys").document("chatMessages")
    private var chatSessionListenerRegistration: ListenerRegistration? = null
    private var chatSessionListenerUserId: Long? = null
    private var chatListenerRegistration: ListenerRegistration? = null
    private var chatListenerSessionId: Long? = null

    private val _chatSessionsStateFlow = MutableStateFlow<List<ChatSession>>(emptyList())
    private val _chatMessagesStateFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    private var currChatSessionId: Long? = null

    fun registerChatSessionListener(userId: Long): StateFlow<List<ChatSession>> {
        if (chatSessionListenerUserId == userId)
            return _chatSessionsStateFlow.asStateFlow()
        //cancelChatSessionListenerRegistration()
        chatSessionListenerRegistration =
            chatSessionReference.whereArrayContains("participantUserIds", userId)
                .whereNotEqualTo("mostRecentMessage", "")
                .addSnapshotListener { documentSnapshots, error ->
                    if (error == null) {
                        val toReturn: MutableList<ChatSession> = mutableListOf()
                        documentSnapshots?.forEach { document ->
                            toReturn.add(document.toObject(ChatSession::class.java))
                        }
                        _chatSessionsStateFlow.update { toReturn }
                    } else {
                        Log.d(TAG, "registerChatSessionListener: ${error.message}")
                    }
                }
        chatSessionListenerUserId = userId
        return _chatSessionsStateFlow.asStateFlow()
    }

    private fun cancelChatSessionListenerRegistration() {
        chatSessionListenerRegistration?.let { it.remove() }
            .also {
                chatSessionListenerRegistration = null
                chatSessionListenerUserId = null
            }
    }

    fun registerChatListener(sessionId: Long): StateFlow<List<ChatMessage>> {
        Log.d(TAG, "registerChatListener: Registering chat listener for sessionId: $sessionId")
        if (chatListenerSessionId == sessionId)
            return _chatMessagesStateFlow.asStateFlow()
        //cancelChatListenerRegistration()
        chatListenerRegistration =
            chatReference.whereEqualTo("sessionId", sessionId)
                .addSnapshotListener { documentSnapshots, error ->
                    if (error == null) {
                        val messages: MutableList<ChatMessage> = mutableListOf()
                        documentSnapshots?.forEach { document ->
                            messages.add(document.toObject(ChatMessage::class.java))
                        }
                        messages.sortBy {
                            it.timestamp
                        }
                        _chatMessagesStateFlow.update { messages }
                    } else {
                        Log.d(TAG, "registerChatListener: ${error.message}")
                    }
                }
        chatListenerSessionId = sessionId
        currChatSessionId = sessionId
        return _chatMessagesStateFlow.asStateFlow()
    }

    private fun cancelChatListenerRegistration() {
        Log.d(TAG, "cancelChatListenerRegistration: Removing listener")
        chatListenerRegistration?.let { it.remove() }
            .also {
                chatListenerRegistration = null
                currChatSessionId = null
                chatListenerSessionId = null
            }
    }

    fun addChatMessage(chatMessage: ChatMessage) {

        CoroutineScope(Dispatchers.IO).launch {
            val newKey = getNewChatMessageKey()
            val toAdd = chatMessage.copy(messageId = newKey)
            chatReference.document().set(toAdd)
            updateChatSessionRecentMessage(chatMessage = toAdd)
        }

    }

    suspend fun deleteJobChats(jobId: Long) {
        val documents = chatSessionReference.whereEqualTo("jobId", jobId).get().await().documents
        val sessionIds = documents.map { it.toObject(ChatSession::class.java) }
        sessionIds.forEach { chatSession ->
            chatSession?.let {
                deleteChatMessagesForSession(chatSession.chatSessionId)
            }
        }
        documents.forEach {
            it?.reference?.delete()
        }
    }

    suspend fun deleteChatSession(sessionId: Long) {
        deleteChatMessagesForSession(sessionId)
        val session =
            chatSessionReference.whereEqualTo("chatSessionId", sessionId).get().await().documents[0]
        session.reference.delete()
    }

    private suspend fun deleteChatMessagesForSession(sessionId: Long) {
        val documents = chatReference.whereEqualTo("sessionId", sessionId).get().await().documents
        documents.forEach {
            it?.reference?.delete()
        }
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
        jobCreatorUserId: Long,
        jobCreatorDisplayName: String,
        currUserId: Long,
        currUserDisplayName: String,
        jobId: Long,
        jobImageUrl: String
    ): Result<Long> {
        try {
            val existingChatSession = chatSessionReference
                .whereEqualTo("jobCreatorUserId", jobCreatorUserId)
                .whereEqualTo("workerUserId", currUserId)
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
                .documents
            if (existingChatSession.isNotEmpty()) {
                val chatSessionId =
                    existingChatSession[0].getDouble("chatSessionId")?.toLong() ?: -1
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

    suspend fun markChatMessageAsRead(messageId: Long) {
        val docs = chatReference.whereEqualTo("messageId", messageId).get().await()
        if (!docs.isEmpty) {
            val message =
                chatReference.whereEqualTo("messageId", messageId).get().await().documents[0]
            message.reference.update("hasBeenRead", true)
        }
    }

    suspend fun getChatSessionById(sessionId: Long): ChatSession? {
        val toReturn =
            chatSessionReference.whereEqualTo("chatSessionId", sessionId).get().await().documents[0]
        return toReturn.toObject(ChatSession::class.java)
    }

    private suspend fun getNewChatSessionKey(): Long {
        var toReturn = 0L
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(chatSessionKeystoreReference)
            toReturn = snapshot.getLong("value")!!
            val newValue = snapshot.getLong("value")!! + 1
            transaction.update(chatSessionKeystoreReference, "value", newValue)
        }.await()
        return toReturn
    }

    private suspend fun getNewChatMessageKey(): Long {
        var toReturn = 0L
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(chatMessageKeystoreReference)
            toReturn = snapshot.getLong("value")!!
            val newValue = snapshot.getLong("value")!! + 1
            transaction.update(chatMessageKeystoreReference, "value", newValue)
        }.await()
        return toReturn
    }
}