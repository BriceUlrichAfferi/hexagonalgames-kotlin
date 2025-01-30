package com.openclassrooms.hexagonal.games.screen.comment

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentFormScreen(
    postId: String,
    navController: NavController,
    viewModel: CommentFormViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var comment by remember { mutableStateOf(TextFieldValue("")) }
    val isButtonEnabled by remember { derivedStateOf { comment.text.isNotBlank() } }
    val errorMessage by viewModel.errorMessage.collectAsState()
    var hasClickedSubmit by remember { mutableStateOf(false) }

    // Show error message if button was clicked and comment is empty
    val showErrorMessage = hasClickedSubmit && comment.text.isBlank()

    errorMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.add_comment)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text(stringResource(id = R.string.write_comment)) },
                isError = showErrorMessage,  // Show error if there is an issue
                modifier = Modifier.fillMaxWidth()
            )

            if (showErrorMessage) {
                Text(
                    text = stringResource(id = R.string.write_comment),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    hasClickedSubmit = true  // Set hasClickedSubmit to true when the button is clicked
                    coroutineScope.launch {
                        viewModel.addComment(postId, comment.text)
                        navController.popBackStack()

                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.write_comment))
            }
        }
    }
}



