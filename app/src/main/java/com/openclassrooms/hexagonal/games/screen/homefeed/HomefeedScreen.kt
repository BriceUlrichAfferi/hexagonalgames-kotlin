package com.openclassrooms.hexagonal.games.screen.homefeed

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import androidx.navigation.NavHostController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomefeedScreen(
  modifier: Modifier = Modifier,
  viewModel: HomefeedViewModel = hiltViewModel(),
  navHostController: NavHostController,
  onPostClick: (Post) -> Unit = { post ->
    Log.d("HomefeedScreen", "Post clicked, id: ${post.id}")
    try {
      Log.d("HexagonalGamesNavHost", "Navigating to post details with id: ${post.id}")
      navHostController.navigate(Screen.PostDetails.createRoute(post.id))
      Log.d("HomefeedScreen", "Navigation attempt for post ${post.id}")
    } catch (e: Exception) {
      Log.e("HomefeedScreen", "Navigation failed for post ${post.id}: ${e.message}", e)
    }
  },

  onSettingsClick: () -> Unit = {},
  onAccountClick: () -> Unit = {},
  onFABClick: () -> Unit = {}
) {
  var showMenu by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(id = R.string.homefeed_fragment_label))
        },
        actions = {
          IconButton(onClick = { showMenu = !showMenu }) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = stringResource(id = R.string.contentDescription_more)
            )
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              onClick = {
                onSettingsClick()
              },
              text = {
                Text(
                  text = stringResource(id = R.string.action_settings)
                )
              }
            )

            DropdownMenuItem(
              onClick = {
                onAccountClick()
              },
              text = {
                Text(
                  text = stringResource(id = R.string.action_account)
                )
              }
            )
          }
        }
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          onFABClick()
        }
      ) {
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = stringResource(id = R.string.description_button_add)
        )
      }
    }
  ) { contentPadding ->
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    Log.d("HomefeedScreen", "Posts collected: ${posts.size}")

    HomefeedList(
      modifier = modifier.padding(contentPadding),
      posts = posts,
      onPostClick = onPostClick
    )
  }
}


@Composable
private fun HomefeedList(
  modifier: Modifier = Modifier,
  posts: List<Post>,
  onPostClick: (Post) -> Unit,
) {
  LazyColumn(
    modifier = modifier.padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(posts) { post ->
      HomefeedCell(
        post = post,
        onPostClick = onPostClick
      )
    }
  }
}

@Composable
private fun HomefeedCell(
  post: Post,
  onPostClick: (Post) -> Unit,
) {
  ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = {
      Log.d("HomefeedCell", "Click event on post: ${post.id}")
      Log.d("HomefeedCell", "About to call onPostClick")

      onPostClick(post) }
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
    ) {
      val author = post.author
      Text(
        text = stringResource(
          id = R.string.by,
          author?.firstName ?: "Unknown",
          author?.lastName ?: "User"
        ),
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = post.title,
        style = MaterialTheme.typography.titleLarge
      )
      if (post.photoUrl.isNullOrEmpty() == false) {
        AsyncImage(
          modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .aspectRatio(ratio = 16 / 9f),
          model = post.photoUrl,
          imageLoader = LocalContext.current.imageLoader.newBuilder()
            .logger(DebugLogger())
            .build(),
          placeholder = ColorPainter(Color.DarkGray),
          contentDescription = "image",
          contentScale = ContentScale.Crop,
        )
      }
      if (post.description.isNullOrEmpty() == false) {
        Text(
          text = post.description,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}


@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HomefeedCellPreview() {
  HexagonalGamesTheme {
    HomefeedCell(
      post = Post(
        id = "1",
        title = "title",
        description = "description",
        photoUrl = null,
        timestamp = 1,
        author = User(
          id = "1",
          firstName = "firstname",
          lastName = "lastname"
        )
      ),
      onPostClick = {}
    )
  }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HomefeedCellImagePreview() {
  HexagonalGamesTheme {
    HomefeedCell(
      post = Post(
        id = "1",
        title = "title",
        description = null,
        photoUrl = "https://picsum.photos/id/85/1080/",
        timestamp = 1,
        author = User(
          id = "1",
          firstName = "firstname",
          lastName = "lastname"
        )
      ),
      onPostClick = {}
    )
  }
}