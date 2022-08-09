package com.baec23.upcycler.repository

import com.baec23.upcycler.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var currUser: User? = null

    //trySignup
    //tryLogin
}