package com.openclassrooms.hexagonal.games.data.repository


import com.google.firebase.auth.FirebaseUser

class UserManager(private val userRepository: UserRepository) {

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return userRepository.signInWithEmailAndPassword(email, password)
    }

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return userRepository.createUserWithEmailAndPassword(email, password)
    }

    suspend fun checkIfEmailExists(email: String): Boolean {
        val signInMethods = userRepository.fetchSignInMethodsForEmail(email)
        return !signInMethods.isNullOrEmpty()
    }

    fun getCurrentUser(): FirebaseUser? {
        return userRepository.getCurrentUser()
    }
}
