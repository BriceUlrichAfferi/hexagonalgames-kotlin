package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountManagementScreen
import com.openclassrooms.hexagonal.games.screen.account.InitialLoginScreen
import com.openclassrooms.hexagonal.games.screen.account.PasswordRecoveryScreen
import com.openclassrooms.hexagonal.games.screen.account.PostDetailsScreen
import com.openclassrooms.hexagonal.games.screen.account.SignIn
import com.openclassrooms.hexagonal.games.screen.account.SigninScaffold
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.screen.account.SigninScreen
import com.openclassrooms.hexagonal.games.screen.comment.CommentFormScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedViewModel
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private lateinit var firebaseAuthManager: FirebaseAuthManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("DebugCheck", "BuildConfig.DEBUG: ${BuildConfig.DEBUG}")

    // Initialize Firebase if not already initialized
    if (FirebaseApp.getApps(this).isEmpty()) {
      FirebaseApp.initializeApp(this)
    }

    // Set up Firebase App Check
    val firebaseAppCheck = FirebaseAppCheck.getInstance()
    firebaseAppCheck.installAppCheckProviderFactory(
      if (BuildConfig.DEBUG) {
        DebugAppCheckProviderFactory.getInstance()
      } else {
        PlayIntegrityAppCheckProviderFactory.getInstance()
      }
    )

    // Fetch and log the App Check token for debugging in debug builds
    if (BuildConfig.DEBUG) {
      Log.d("AppCheck", "Attempting to fetch debug token")
      firebaseAppCheck.getAppCheckToken(true).addOnSuccessListener { appCheckTokenResponse ->
        val token = appCheckTokenResponse.token
        Log.d("AppCheck", "Debug Token: $token")
      }.addOnFailureListener { exception ->
        Log.e("AppCheck", "Failed to get debug token", exception)
      }
    } else {
      Log.d("AppCheck", "Running in release mode, no debug token fetch")
    }

    firebaseAuthManager = FirebaseAuthManager(this)

    setContent {
      val navController = rememberNavController()

      HexagonalGamesTheme {
        HexagonalGamesNavHost(navHostController = navController)
      }
    }
  }

  // Function to trigger Firebase Authentication sign-in flow
  fun signInWithFirebase(onSuccess: () -> Unit) {
    firebaseAuthManager.signInWithFirebase(onSuccess)
  }
}

@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
  NavHost(
    navController = navHostController,
    startDestination = Screen.Homefeed.route
  ) {
    composable(route = Screen.Homefeed.route) {
      HomefeedScreen(
        onPostClick = { post ->
          Log.d("HexagonalGamesNavHost", "Navigating to post details with id: ${post.id}")
          navHostController.navigate(Screen.PostDetails.createRoute(post.id))
        },
        onSettingsClick = {
          navHostController.navigate(Screen.Settings.route)
        },
        onAccountClick = {
          navHostController.navigate(Screen.SigningScreen.route)
        },
        onFABClick = {
          navHostController.navigate(Screen.AddPost.route)
        },
        navHostController = navHostController
      )
    }

    composable(route = Screen.AddPost.route) {
      AddScreen(
        onBackClick = { navHostController.navigateUp() },
        onSaveClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.Settings.route) {
      SettingsScreen(
        onBackClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.SigningScreen.route) {
      SigninScreen(
        onLoginSuccess = { navHostController.navigateUp() },
        navController = navHostController // Pass the NavHostController here
      )
    }
    composable(route = Screen.InitialLoginScreen.route) {
      InitialLoginScreen(
        onSignInClick = {
          navHostController.navigate(Screen.SignIn.route) {
            // This ensures the back stack only includes InitialLoginScreen and HomefeedScreen
            popUpTo(Screen.Homefeed.route) { inclusive = false }
          }
        },
        onSignUpClick = {
          navHostController.navigate(Screen.SigninScaffold.route) {
            // This ensures the back stack only includes InitialLoginScreen and HomefeedScreen
            popUpTo(Screen.Homefeed.route) { inclusive = false }
          }
        },
        navController = navHostController
      )
    }

    composable(route = Screen.SignIn.route) {
      SignIn(
        navController = navHostController // Pass the NavHostController here
      )
    }

    composable(route = Screen.AccountManagementScreen.route) {
      AccountManagementScreen(
        navController = navHostController,
        onLogout = { navHostController.navigate(Screen.InitialLoginScreen.route) },
        onDeleteAccount = { navHostController.navigate(Screen.InitialLoginScreen.route) }
      )
    }
    composable(route = Screen.SigninScaffold.route) {
      SigninScaffold(
        onLoginSuccess = { navHostController.navigateUp() },
        navController = navHostController // Pass the NavHostController here
      )
    }

    composable(
      route = Screen.PostDetails.route,
      arguments = Screen.PostDetails.navArguments
    ) { backStackEntry ->
      val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
      val viewModel: HomefeedViewModel = hiltViewModel()

      // Use MutableState to hold the post data
      var post by remember { mutableStateOf<Post?>(null) }

      LaunchedEffect(postId) {
        viewModel.getPostById(postId).collect { fetchedPost ->
          post = fetchedPost
        }
      }

      // Show content only when post is not null
      if (post != null) {
        PostDetailsScreen(post = post!!, navController = navHostController)
      }
    }
    composable(route = Screen.PasswordRecovery.route) {
      PasswordRecoveryScreen(navController = navHostController)
    }
    composable(
      route = Screen.CommentFormScreen.route,
      arguments = Screen.CommentFormScreen.navArguments
    ) { backStackEntry ->
      val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
      CommentFormScreen(
        postId = postId,
        navController = navHostController // Pass the NavHostController here
      )
    }
  }
}