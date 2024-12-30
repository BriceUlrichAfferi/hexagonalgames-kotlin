package com.openclassrooms.hexagonal.games.domain.model


data class Comment(
    val id: String = "",
    val postId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val author: User? = null // Add author property of type User
)

