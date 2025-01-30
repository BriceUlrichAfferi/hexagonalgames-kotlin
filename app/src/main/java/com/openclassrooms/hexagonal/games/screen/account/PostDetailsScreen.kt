package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedViewModel
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    post: Post,
    navController: NavController,
    viewModel: HomefeedViewModel = hiltViewModel()
) {
    LaunchedEffect(post.id) {
        viewModel.getCommentsForPost(post.id)
    }
    val comments by viewModel.comments.collectAsState(initial = emptyList())
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    HexagonalGamesTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(post.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack() // Use popBackStack for going back
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                val accountMandatoryMessage = stringResource(R.string.account_mandatory_for_comment)
                FloatingActionButton(
                    onClick = {
                        Log.d("PostDetailsScreen", "Add comment button clicked")
                        if (auth.currentUser == null) {
                            Log.d("PostDetailsScreen", "User is not logged in, showing toast")
                            Toast.makeText(context, accountMandatoryMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("PostDetailsScreen", "User is logged in, navigating to comment form")
                            navController.navigate(Screen.CommentFormScreen.createRoute(post.id))
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Comment")
                }
            }
        ) { paddingValues ->
            PostDetailsContent(
                post = post,
                comments = comments,
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun PostDetailsContent(
    post: Post,
    comments: List<Comment>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Post Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Author Information
                val author = post.author
                Text(
                    text = stringResource(
                        id = R.string.by,
                        author?.firstName ?: "Unknown",
                        author?.lastName ?: "User"
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                // Title
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Image
                if (!post.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        model = post.photoUrl,
                        imageLoader = LocalContext.current.imageLoader.newBuilder()
                            .logger(DebugLogger())
                            .build(),
                        placeholder = ColorPainter(Color.LightGray),
                        contentDescription = "Post Image",
                        contentScale = ContentScale.Crop
                    )
                }

                // Description
                if (!post.description.isNullOrEmpty()) {
                    Text(
                        text = post.description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Add Comment Section
        if (comments.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // This ensures comments take up remaining space if needed
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                }
            }
        } else {
            Text(stringResource(id = R.string.no_comment), modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val formattedDate = dateFormat.format(Date(comment.timestamp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(

                text = stringResource(id = R.string.commented_at, formattedDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PostDetailsPreview() {
    HexagonalGamesTheme {
        val navController = rememberNavController() // Mock NavController for preview
        PostDetailsScreen(
            post = Post(
                id = "1",
                title = "A Long Title That Might Overflow",
                description = "This is a detailed description of the post which might span across several lines.",
                photoUrl = "https://picsum.photos/id/85/1080/",
                timestamp = 1,
                author = User(
                    id = "1",
                    firstName = "firstname",
                    lastName = "lastname"
                )
            ),
            navController = navController
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PostDetailsNoImagePreview() {
    HexagonalGamesTheme {
        val navController = rememberNavController() // Mock NavController for preview
        PostDetailsScreen(
            post = Post(
                id = "1",
                title = "title",
                description = "description without image",
                photoUrl = null,
                timestamp = 1,
                author = User(
                    id = "1",
                    firstName = "firstname",
                    lastName = "lastname"
                )
            ),
            navController = navController
        )
    }
}