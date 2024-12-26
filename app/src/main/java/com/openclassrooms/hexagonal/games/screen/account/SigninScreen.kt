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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigninScreen(onLoginSuccess: () -> Unit, navController: NavController) {
    val context = LocalContext.current
    val currentStep = remember { mutableStateOf(1) }

    // Sign-in state variables
    val email = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentStep.value) {
        // Handle any side effects when currentStep changes
        println("LaunchedEffect for step: ${currentStep.value}")

        println("Step changed to: ${currentStep.value}")
        // Additional logic here if needed
    }

    Scaffold(
        topBar = {
            if (currentStep.value > 0) { // Show back button only for steps 1 and above
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep.value > 0) {
                                currentStep.value -= 1 // Go back to the previous step
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
                0 -> InitialLoginScreen(
                    onSignInClick = { currentStep.value = 1 },
                    navController = navController // Pass navController here
                )
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
                                        onLoginSuccess()
                                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                    } else {
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