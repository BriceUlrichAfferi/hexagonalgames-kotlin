package com.openclassrooms.hexagonal.games.screen.account

import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigninScreen(onLoginSuccess: () -> Unit, navController: NavController) {
    Log.d("SigninScreen", "SigninScreen is recomposed")

    val context = LocalContext.current
    val currentStep = remember { mutableStateOf(1) } // Starting from 0 for initial login screen

    // Sign-in state variables
    val password = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Check authentication status
    val toastShown = remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (!toastShown.value) {
            toastShown.value = true
            withContext(Dispatchers.Main) {
                if (currentUser != null) {
                    // If the user is already authenticated, show a message and navigate to home
                    Toast.makeText(context, "You are already authenticated!", Toast.LENGTH_SHORT).show()
                    //navController.navigate(Screen.Homefeed.route) // Corrected route to Homefeed
                    navController.navigate(Screen.AccountManagementScreen.route) // Navigate to AccountManagementScreen

                } else {
                    // If the user is not authenticated, show a different message
                    Toast.makeText(context, "Please register or sign in", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.InitialLoginScreen.route) // Navigate to AccountManagementScreen

                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentStep.value > 0) {
                val title = when (currentStep.value) {
                    1 -> R.string.sign_in
                    2 -> R.string.sign_in
                    else -> R.string.app_name // Default title, assuming you have one
                }
                TopAppBar(
                    title = { Text(text = stringResource(id = title)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep.value > 0) currentStep.value -= 1 // Go back to the previous step
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
                1 -> PasswordInputScreen(
                    password = password.value,
                    onPasswordChange = { password.value = it },
                    onLogin = {
                        // Validate password and login
                        passwordError.value = when {
                            password.value.isBlank() -> "Password cannot be empty"
                            password.value.length < 6 -> "Password must be at least 6 characters"
                            else -> null
                        }

                        if (passwordError.value == null) {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                // Proceed with login if the user is authenticated
                                onLoginSuccess()
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Homefeed.route) // Corrected route to Homefeed
                            } else {
                                passwordError.value = "Authentication failed"
                                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}
