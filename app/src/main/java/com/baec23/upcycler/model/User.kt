package com.baec23.upcycler.model

data class User(
    val id: Long = 0,
    val loginId: String = "",
    val password: String = "",
    val displayName: String = "",
    val lastLoginTimestamp: Long = 0
)
