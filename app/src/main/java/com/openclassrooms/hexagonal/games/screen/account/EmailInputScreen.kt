package com.openclassrooms.hexagonal.games.screen.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.hexagonal.games.R

@Composable
fun EmailInputScreen(
    email: String,
    emailError: String?,  // Add emailError as a parameter
    onEmailChange: (String) -> Unit,
    onValidateEmail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_email),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(id = R.string.email)) },
            isError = emailError != null,  // Show error if emailError is not null
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onValidateEmail) {
            Text(text = stringResource(id = R.string.next))
        }
    }
}
