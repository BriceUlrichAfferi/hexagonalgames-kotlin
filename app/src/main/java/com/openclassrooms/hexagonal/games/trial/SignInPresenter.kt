package com.openclassrooms.hexagonal.games.trial

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class SignInPresenter(
    private val view: SignInContract.View,
    private val repository: SignInRepository
) {

    private var email: String = ""
    private var name: String = ""
    private var surname: String = ""
    private var password: String = ""

    // Method called when the email is entered
    fun onEmailEntered(email: String) {
        this.email = email
        onValidateEmail() // Trigger validation on every input change
    }

    // Method called to validate the email
    fun onValidateEmail() {
        if (email.isBlank()) {
            view.showEmailValidationError("Email cannot be empty")
            return
        }

        // Start coroutine for checking email existence
        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                repository.checkIfUserExists(email)
            } catch (e: Exception) {
                Result.failure(e)
            }

            // Handle the result on the main thread
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { exists ->
                        if (exists) {
                            view.navigateToStep(3) // Navigate to password input if user exists
                        } else {
                            view.navigateToStep(2) // Navigate to name/surname input if user doesn't exist
                        }
                    },
                    onFailure = { error ->
                        when (error) {
                            is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                                // If this exception is thrown, it means the email already exists
                                Log.e("SignInPresenter", "Email already exists: ${error.message}", error)
                                view.showErrorMessage("This email is already registered.")
                            }
                            else -> {
                                Log.e("SignInPresenter", "Failed to check email: ${error.message}", error)
                                view.showErrorMessage("Failed to check email: ${error.message}")
                            }
                        }
                    }
                )
            }
        }
    }

    // Other methods for name, surname, and password handling
    fun onNameChanged(name: String) {
        this.name = name
    }

    fun onSurnameChanged(surname: String) {
        this.surname = surname
    }

    fun onProceedToPassword() {
        if (name.isBlank() || surname.isBlank()) {
            view.showErrorMessage("Name and surname cannot be empty")
            return
        }
        view.navigateToStep(3) // Move to password input step
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }

    fun onPasswordEntered() {
        if (password.length < 6) {
            view.showErrorMessage("Password must be at least 6 characters")
            return
        }

        // Handle password creation/login
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.createUser(email, password)
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = {
                        view.showSuccessMessage("Account created successfully")
                    },
                    onFailure = { error ->
                        view.showErrorMessage(error.message ?: "An error occurred")
                    }
                )
            }
        }
    }
}