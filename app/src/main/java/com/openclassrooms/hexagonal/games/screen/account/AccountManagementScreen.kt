package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current
    val message = stringResource(id = R.string.account_deleted_successfully)
    val log_out = stringResource(id = R.string.account_logged_Out)

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Handle logout
    fun logout() {
        auth.signOut()
        onLogout() // Navigate to InitialLoginScreen
        Toast.makeText(navController.context, log_out, Toast.LENGTH_SHORT).show()
    }

    // Handle delete account
    fun deleteAccount() {
        if (currentUser != null) {
            isDeleting = true
            currentUser.delete()
                .addOnCompleteListener { task ->
                    isDeleting = false
                    if (task.isSuccessful) {
                        onDeleteAccount() // Navigate to InitialLoginScreen
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(navController.context, "Error deleting account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.account_managements)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Homefeed.route) {
                            // Ensure the back stack is cleared up to the home feed
                            popUpTo(Screen.Homefeed.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display current user information (optional)
                currentUser?.let {
                    Text(text = stringResource(id = R.string.logged_as, it.email ?: "Unknown"),
                        style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Log out button
                Button(
                    onClick = { logout() },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(text = stringResource(id = R.string.log_out))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delete account button
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(id = R.string.delete_account), color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    )

    // Confirmation dialog for account deletion
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = stringResource(id = R.string.deletion_account_dialog)) },
            text = { Text(text = stringResource(id = R.string.deletion_account_sure)) },
            confirmButton = {
                TextButton(
                    onClick = { deleteAccount() }
                ) {
                    Text(stringResource(id = R.string.delete_account))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Loading indicator while deleting account
    if (isDeleting) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun AccountManagementScreenPreview() {
    HexagonalGamesTheme {
        AccountManagementScreen(
            navController = NavController(context = LocalContext.current),
            onLogout = {},
            onDeleteAccount = {}
        )
    }
}
