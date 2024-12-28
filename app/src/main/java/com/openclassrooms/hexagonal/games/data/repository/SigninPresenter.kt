package com.openclassrooms.hexagonal.games.presentation

import android.widget.Toast
import com.openclassrooms.hexagonal.games.data.repository.SigninContract
import com.openclassrooms.hexagonal.games.data.repository.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SigninPresenter(
    private val view: SigninContract.View,
    private val userManager: UserManager
) : SigninContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onEmailChanged(email: String) {
        // Handle email change logic
        if (email.isBlank()) {
            view.showEmailError("Email cannot be empty")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError("Please enter a valid email address")
        } else {
            // Email is valid, now check if it exists
            checkIfEmailExists(email)
        }
    }

    private fun checkIfEmailExists(email: String) {
        // Run the check in a coroutine
        scope.launch {
            val isExistingUser = withContext(Dispatchers.IO) {
                // This calls the suspend function in the IO dispatcher
                userManager.checkIfEmailExists(email)
            }
            if (isExistingUser) {
                view.showPasswordError("Password cannot be empty")
            }
        }
    }

    override fun onPasswordChanged(password: String) {
        // Handle password change logic
        if (password.isBlank()) {
            view.showPasswordError("Password cannot be empty")
        } else if (password.length < 6) {
            view.showPasswordError("Password must be at least 6 characters")
        }
    }

    override fun onSignInClicked(email: String, password: String) {
        // Handle sign-in logic
        if (email.isNotBlank() && password.isNotBlank()) {
            scope.launch {
                val user = withContext(Dispatchers.IO) {
                    userManager.loginUser(email, password)
                }
                if (user != null) {  // Check if the user is not null
                    view.showToast("Login Successful")
                    view.navigateToHomeScreen()
                } else {
                    view.showToast("Login Failed")
                }
            }
        }
    }

    override fun onRegisterClicked(email: String, password: String) {
        // Handle registration logic
        if (email.isNotBlank() && password.isNotBlank()) {
            scope.launch {
                val user = withContext(Dispatchers.IO) {
                    userManager.registerUser(email, password)
                }
                if (user != null) {  // Check if the user is not null
                    view.showToast("Account Created and Login Successful")
                    view.navigateToHomeScreen()
                } else {
                    view.showToast("Registration Failed")
                }
            }
        }
    }
}
