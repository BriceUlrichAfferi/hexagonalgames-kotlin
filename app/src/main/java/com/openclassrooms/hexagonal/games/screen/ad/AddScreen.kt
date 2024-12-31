package com.openclassrooms.hexagonal.games.screen.ad

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
  modifier: Modifier = Modifier,
  viewModel: AddViewModel = hiltViewModel(),
  onBackClick: () -> Unit,
  onSaveClick: () -> Unit
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(id = R.string.add_fragment_label))
        },
        navigationIcon = {
          IconButton(onClick = {
            onBackClick()
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.contentDescription_go_back)
            )
          }
        }
      )
    }
  ) { contentPadding ->
    val post by viewModel.post.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    CreatePost(
      modifier = Modifier.padding(contentPadding),
      error = error,
      title = post.title ?: "",
      onTitleChanged = { viewModel.onAction(FormEvent.TitleChanged(it)) },
      description = post.description ?: "",
      onDescriptionChanged = { viewModel.onAction(FormEvent.DescriptionChanged(it)) },
      onSaveClicked = {
        viewModel.addPost()
        onSaveClick()
      }
    )
  }
}

@Composable
private fun CreatePost(
  modifier: Modifier = Modifier,
  title: String,
  onTitleChanged: (String) -> Unit,
  description: String,
  onDescriptionChanged: (String) -> Unit,
  onSaveClicked: () -> Unit,
  error: FormError?
) {
  val context = LocalContext.current // Get context
  var imageUri by remember { mutableStateOf<Uri?>(null) }

  val pickImageLauncherFor13Plus = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri -> imageUri = uri }

  val pickImageLauncherForOlder = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri -> imageUri = uri }

  Column(
    modifier = modifier
      .padding(16.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = title,
        onValueChange = { onTitleChanged(it) },
        label = { Text(stringResource(id = R.string.hint_title)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true
      )

      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = description,
        onValueChange = { onDescriptionChanged(it) },
        label = { Text(stringResource(id = R.string.hint_description)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
      )

      imageUri?.let {
        Image(
          painter = rememberImagePainter(it),
          contentDescription = "Selected Image",
          modifier = Modifier.size(150.dp)
        )
      }

      Button(modifier = Modifier.padding(top = 22.dp), onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          val pickRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
          pickImageLauncherFor13Plus.launch(pickRequest)
        } else {
          pickImageLauncherForOlder.launch("image/*")
        }
      }) {

       Text(text = stringResource(id = R.string.select_photo)) }

      }
    }

    Button(
      enabled = error == null,
      onClick = {
        // Call save with context to display a toast
        savePostToFirestore(
          title = title,
          description = description,
          imageUri = imageUri,
          onComplete = { success, errorMessage ->
            val toastMessage = if (success) {
              "Post saved successfully!"
            } else {
              "Failed to save post: ${errorMessage ?: "Unknown error"}"
            }
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            onSaveClicked()
          }
        )
      }
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.action_save)
      )
    }
  }



fun savePostToFirestore(
  title: String,
  description: String,
  imageUri: Uri?,
  onComplete: (Boolean, String?) -> Unit
) {
  val firestore = FirebaseFirestore.getInstance()
  val storage = FirebaseStorage.getInstance()

  // Create a unique ID for the post
  val postId = UUID.randomUUID().toString()

  if (imageUri != null) {
    // Upload the image to Firebase Storage
    val storageRef = storage.reference.child("images/$postId.jpg")
    storageRef.putFile(imageUri)
      .addOnSuccessListener { taskSnapshot ->
        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
          // Save post data with the image URL
          val post = mapOf(
            "title" to title,
            "description" to description,
            "imageUrl" to downloadUri.toString()
          )
          firestore.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener {
              onComplete(true, null)
            }
            .addOnFailureListener { e ->
              onComplete(false, e.message)
            }
        }
      }
      .addOnFailureListener { e ->
        onComplete(false, e.message)
      }
  } else {
    // Save post data without an image
    val post = mapOf(
      "title" to title,
      "description" to description
    )
    firestore.collection("posts").document(postId)
      .set(post)
      .addOnSuccessListener {
        onComplete(true, null)
      }
      .addOnFailureListener { e ->
        onComplete(false, e.message)
      }
  }
}


@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostPreview() {
  HexagonalGamesTheme {
    /*CreatePost(
      title = "test",
      onTitleChanged = { },
      description = "description",
      onDescriptionChanged = { },
      onSaveClicked = { },
      error = null
    )*/
  }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostErrorPreview() {
  HexagonalGamesTheme {
    /*CreatePost(
      title = "test",
      onTitleChanged = { },
      description = "description",
      onDescriptionChanged = { },
      onSaveClicked = { },
      error = FormError.TitleError
    )*/
  }
}
