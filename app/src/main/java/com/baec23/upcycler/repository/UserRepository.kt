package com.baec23.upcycler.repository

import com.baec23.upcycler.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityScoped
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    private val usersReference: CollectionReference = firestore.collection("users")
    private val keyStoreReference: DocumentReference =
        firestore.collection("keys").document("users")
    private var currUser: User? = null

    suspend fun tryLogin(loginId: String, password: String): Result<User> {
        delay(2000L)
        val documentSnapshots = usersReference
            .whereEqualTo("loginId", loginId)
            .get()
            .await()
            .documents

        for (document in documentSnapshots) {
            val user = document.toObject(User::class.java)
            return if (user != null && user.password == password) {
                currUser = user
                Result.success(user)
            } else
                Result.failure(Exception("Password Incorrect"))
        }
        return Result.failure(Exception("No User Found for LoginId: $loginId"))
    }

    suspend fun trySignup(loginId: String, password: String, displayName: String): Result<String> {
        delay(2000L)
        if (doesDuplicateIdExist(loginId))
            return Result.failure(Exception("LoginId: $loginId Already Exists"))
        val userId = getNewKey()
        val userToAdd = User(userId, loginId, password, displayName)
        return try {
            usersReference
                .document()
                .set(userToAdd)
                .await()
            Result.success("Success")
        } catch (e: Exception) {
            Result.failure(Exception())
        }
    }

    private suspend fun doesDuplicateIdExist(loginId: String): Boolean {
        val documentSnapshot = usersReference
            .whereEqualTo("loginId", loginId)
            .get()
            .await()
            .documents
        return documentSnapshot.isNotEmpty()
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