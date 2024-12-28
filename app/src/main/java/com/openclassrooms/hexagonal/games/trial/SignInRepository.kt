package com.openclassrooms.hexagonal.games.trial

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInRepository {

    // Function to check if the user exists by email
    suspend fun checkIfUserExists(email: String): Result<Boolean> {
        return try {
            val result = FirebaseAuth.getInstance()
                .fetchSignInMethodsForEmail(email)
                .await()

            Result.success(result.signInMethods?.isNotEmpty() == true)
        } catch (exception: Exception) {
            when (exception) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                    Result.success(false) // Email doesn't exist
                }
                else -> {
                    Result.failure(exception) // Other errors
                }
            }
        }
    }

    // Function to create a new user with email and password
    suspend fun createUser(email: String, password: String): Result<Unit> {
        return try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}