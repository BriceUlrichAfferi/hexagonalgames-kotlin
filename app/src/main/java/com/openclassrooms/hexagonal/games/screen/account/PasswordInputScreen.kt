package com.openclassrooms.hexagonal.games.screen.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordInputScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    // State for error messages
    val passwordError = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter your password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,  // No delegate needed here, it comes from the parent
            onValueChange = onPasswordChange, // Updates password state in parent
            label = { Text("Password") },
            isError = passwordError.value != null,  // Error condition
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        // Show error message only if there's an error
        if (passwordError.value != null) {
            Text(
                text = passwordError.value ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Validate password on button click
            passwordError.value = when {
                password.isBlank() -> "Password cannot be empty"
                password.length < 6 -> "Password must be at least 6 characters"
                else -> null
            }

            // Only proceed to login if no error
            if (passwordError.value == null) {
                onLogin() // Proceed with login
            }
        }) {
            Text("Login")
        }
    }
}
