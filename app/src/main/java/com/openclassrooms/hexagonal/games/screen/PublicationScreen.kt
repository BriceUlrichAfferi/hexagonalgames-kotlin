package com.openclassrooms.hexagonal.games.screen

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.openclassrooms.hexagonal.games.R

@Composable
fun PublicationScreen() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Register the activity result contract for picking an image (Android 13+)
    val pickImageLauncherFor13Plus = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Handle the selected image URI
        imageUri = uri
    }

    // Register the activity result contract for older Android versions (GetContent)
    val pickImageLauncherForOlder = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Handle the selected image URI
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Title")
        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Description")
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Display selected image if available
        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier.size(150.dp)
            )
        } ?: run {
            // Placeholder image when no image is selected

            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(1.dp, Color.Black, shape = RectangleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "Placeholder Image",
                    modifier = Modifier.fillMaxSize() // Fill the entire Box
                )
                Text(
                    text = "Your Image will display Here",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.White.copy(alpha = 0.7f)) // Semi-transparent background for visibility
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Button to upload photo
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+ (API level 33), use PickVisualMediaRequest
                val pickRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                pickImageLauncherFor13Plus.launch(pickRequest)
            } else {
                // For older Android versions, use GetContent with MIME type
                pickImageLauncherForOlder.launch("image/*")
            }
        }) {
            Text("Upload Photo")
        }

        Button(onClick = {
            // Handle submission of the form
            println("Title: $title, Description: $description, Image URI: $imageUri")
        }) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PublicationScreen()
}
