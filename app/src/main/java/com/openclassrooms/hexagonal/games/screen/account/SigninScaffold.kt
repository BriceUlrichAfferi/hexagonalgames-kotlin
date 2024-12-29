package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigninScaffold(
    onLoginSuccess: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }


    val currentStep = remember { mutableStateOf(1) } // Starting from 0 for initial login screen

    // Sign-in state variables
    val password = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Check authentication status
    val toastShown = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentStep.value > 0) {
                val title = when (currentStep.value) {
                    1 -> R.string.sign_up
                    2 -> R.string.sign_up
                    else -> R.string.sign_up // Default title, assuming you have one
                }
                TopAppBar(
                    title = { Text(text = stringResource(id = title)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep.value > 1) {
                                currentStep.value -= 1 // Go back to the previous step
                            } else {
                                navController.navigate(Screen.Homefeed.route)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.contentDescription_go_back)
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentStep.value) {
                1 -> EmailInputScreen(
                    email = email.value,
                    emailError = emailError.value,
                    onEmailChange = { email.value = it },
                    onValidateEmail = {
                        emailError.value = when {
                            email.value.isBlank() -> "Email cannot be empty"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> "Please enter a valid email address"
                            else -> null
                        }

                        if (emailError.value == null) {
                            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val isExistingUser = task.result?.signInMethods?.isNotEmpty() == true
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
                        if (name.value.isNotBlank() && surname.value.isNotBlank()) {
                            currentStep.value = 3
                        }
                    }
                )
                3 -> PasswordInputScreen(
                    password = password.value,
                    onPasswordChange = { password.value = it },
                    onLogin = {
                        passwordError.value = when {
                            password.value.isBlank() -> "Password cannot be empty"
                            password.value.length < 6 -> "Password must be at least 6 characters"
                            else -> null
                        }

                        if (passwordError.value == null) {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        user?.let {
                                            // Here, we save the name and surname to Firestore
                                            val firestore = FirebaseFirestore.getInstance()
                                            val userRef = firestore.collection("users").document(it.uid)
                                            userRef.set(mapOf(
                                                "firstName" to name.value,
                                                "lastName" to surname.value
                                            )).addOnSuccessListener {
                                                onLoginSuccess()
                                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener { e ->
                                                // Handle failure to write to Firestore
                                                Toast.makeText(context, "Failed to save user details: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        // If creation fails, attempt to sign in for existing users
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                                            .addOnCompleteListener { signInTask ->
                                                if (signInTask.isSuccessful) {
                                                    onLoginSuccess()
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
    }
}
