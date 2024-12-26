package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SigninScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current

    // State management
    val currentStep = remember { mutableStateOf(1) }
    val email = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }

    when (currentStep.value) {
        1 -> EmailInputScreen(
            email = email.value,
            emailError = emailError.value,
            onEmailChange = { email.value = it },
            onValidateEmail = {
                // Validate email
                emailError.value = when {
                    email.value.isBlank() -> "Email cannot be empty"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> "Please enter a valid email address"
                    else -> null
                }

                // If email is valid, check if the email is already registered
                if (emailError.value == null) {
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val isExistingUser = task.result?.signInMethods?.isNotEmpty() == true
                                // If the email is for an existing user, go to password screen, otherwise go to name-surname screen
                                currentStep.value = if (isExistingUser) 3 else 2
                            } else {
                                emailError.value = "Error: ${task.exception?.message}"
                            }
                        }
                }
            }
        )

        2 -> NameSurnameInputScreen(
            name = name.value,
            surname = surname.value,
            onNameChange = { name.value = it },
            onSurnameChange = { surname.value = it },
            onNext = {
                // Validate name and surname
                var nameError: String? = null
                var surnameError: String? = null

                if (name.value.isBlank()) {
                    nameError = "Name cannot be empty"
                }

                if (surname.value.isBlank()) {
                    surnameError = "Surname cannot be empty"
                }

                // If no errors, proceed to next screen
                if (nameError == null && surnameError == null) {
                    currentStep.value = 3
                } else {
                    // Update errors in the state to show them under the fields
                    nameError = nameError
                    surnameError = surnameError
                }
            }
        )


        3 -> PasswordInputScreen(
            password = password.value,
            onPasswordChange = { password.value = it },
            onLogin = {
                // Validate password and login
                passwordError.value = when {
                    password.value.isBlank() -> "Password cannot be empty"
                    password.value.length < 6 -> "Password must be at least 6 characters"
                    else -> null
                }

                // If password is valid, try to sign in or register
                if (passwordError.value == null) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // After successful registration, proceed to login
                                onLoginSuccess() // Proceed to login success
                                // Show Toast message on successful login
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            } else {
                                // If user exists, attempt to log in instead of registering
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                                    .addOnCompleteListener { signInTask ->
                                        if (signInTask.isSuccessful) {
                                            onLoginSuccess() // Proceed to login success
                                            // Show Toast message on successful login
                                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                        } else {
                                            passwordError.value = "Error: ${signInTask.exception?.message}"
                                        }
                                    }
                            }
                        }
                }
            }
        )
    }
}
