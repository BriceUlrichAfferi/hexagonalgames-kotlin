package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.hexagonal.games.R


@Composable
fun NameSurnameInputScreen(
    name: String,
    surname: String,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    // State for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_name_surname),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Name Input
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(id = R.string.name)) },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        nameError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Surname Input
        OutlinedTextField(
            value = surname,
            onValueChange = onSurnameChange,
            label = { Text("Surname") },
            isError = surnameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        surnameError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Next Button to proceed to password screen
        Button(
            onClick = {
                // Validate name and surname
                nameError = when {
                    name.isBlank() -> "Name cannot be empty"
                    else -> null
                }

                surnameError = when {
                    surname.isBlank() -> "Surname cannot be empty"
                    else -> null
                }

                // If there are no errors, proceed to next screen
                if (nameError == null && surnameError == null) {
                    onNext()
                }
            }
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
}