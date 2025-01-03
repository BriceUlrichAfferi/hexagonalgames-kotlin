package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(
    navController: NavController
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }
    val currentStep = remember { mutableStateOf(1) } // Step 1 for email, Step 2 for password
    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.sign_in)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep.value > 1) {
                            currentStep.value -= 1 // Go back to the previous step
                        } else {
                            navController.navigate(Screen.InitialLoginScreen.route)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.sign_in),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step 1: Email input
            if (currentStep.value == 1) {
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email Address") },
                    isError = emailError.value != null,
                    modifier = Modifier.fillMaxWidth()
                )
                emailError.value?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        emailError.value = when {
                            email.value.isBlank() -> "Email cannot be empty"
                            !isValidEmail(email.value) -> "Email not valid"
                            else -> null
                        }
                        if (emailError.value == null) {
                            currentStep.value = 2 // Proceed to password input step
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next", color = Color.White)
                }
            }

            // Step 2: Password input
            if (currentStep.value == 2) {
                // Display the welcome back message
                // Display the welcome back message with bold email
                val welcomeBackText = buildAnnotatedString {
                    val welcomeText = stringResource(R.string.welcome_back_message, email.value)
                    append(welcomeText)
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        start = welcomeText.indexOf(email.value),
                        end = welcomeText.indexOf(email.value) + email.value.length
                    )
                }

                Text(
                    text = welcomeBackText,
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input with visibility toggle
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    isError = passwordError.value != null,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )
                passwordError.value?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In button
                Button(
                    onClick = {
                        // Validate email and password
                        passwordError.value = if (password.value.isBlank()) "Password cannot be empty" else null

                        if (emailError.value == null && passwordError.value == null) {
                            auth.signInWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate(Screen.Homefeed.route)
                                        Toast.makeText(navController.context, "Sign in successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(navController.context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.sign_in), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password clickable text
                TextButton(onClick = {
                    navController.navigate(Screen.PasswordRecovery.route)
                }) {
                    Text("Trouble signing in?", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}
