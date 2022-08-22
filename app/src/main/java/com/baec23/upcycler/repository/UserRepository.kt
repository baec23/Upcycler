package com.baec23.upcycler.repository

import com.baec23.upcycler.model.User
import com.baec23.upcycler.util.DateConverter.isWithinDays
import com.baec23.upcycler.util.LOGIN_EXPIRATION_DAYS
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityScoped
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersReference: CollectionReference = firestore.collection("users")
    private val keyStoreReference: DocumentReference =
        firestore.collection("keys").document("users")

    var currUser: User? = null

    suspend fun tryLogin(loginId: String, password: String): Result<User> {
        val documentSnapshots = usersReference
            .whereEqualTo("loginId", loginId)
            .get()
            .await()
            .documents

        for (document in documentSnapshots) {
            val user = document.toObject(User::class.java)
            return if (user != null && user.password == password) {
                currUser = user
                updateUserLastLogin(user.id)
                Result.success(user)
            } else
                Result.failure(Exception("Password Incorrect"))
        }
        return Result.failure(Exception("No User Found for LoginId: $loginId"))
    }

    suspend fun trySavedLogin(savedUserId: Int): Result<User> {
        val savedUser = getUserById(savedUserId).getOrElse {
            return Result.failure(it)
        }
        return if (savedUser.lastLoginTimestamp.isWithinDays(
                System.currentTimeMillis(),
                LOGIN_EXPIRATION_DAYS
            )
        ) {
            currUser = savedUser
            updateUserLastLogin(savedUser.id)
            Result.success(savedUser)
        } else
            Result.failure(Exception("Last login was over $LOGIN_EXPIRATION_DAYS ago"))
    }

    suspend fun trySignup(loginId: String, password: String, displayName: String): Result<String> {
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

    fun logout(){
        currUser = null
    }

    suspend fun getUserById(userId: Int): Result<User> {
        val result = usersReference.whereEqualTo("id", userId).get().await().documents
        if (result.size == 1) {
            val toReturn = result[0].toObject(User::class.java)
            if (toReturn != null)
                return Result.success(toReturn)
        }
        return Result.failure(Exception("Failed to load user!"))
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

    private suspend fun updateUserLastLogin(userId: Int) {
        val userDocSnap =
            usersReference.whereEqualTo("id", userId).get().await().documents[0]
        if (userDocSnap != null) {
            val docRef = userDocSnap.reference
            docRef.update("lastLoginTimestamp", System.currentTimeMillis())
        }
    }
}