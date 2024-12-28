package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountManagementScreen
import com.openclassrooms.hexagonal.games.screen.account.InitialLoginScreen
import com.openclassrooms.hexagonal.games.screen.account.SignIn
import com.openclassrooms.hexagonal.games.screen.account.SigninScaffold
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.screen.account.SigninScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private lateinit var firebaseAuthManager: FirebaseAuthManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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
        onPostClick = {},
        onSettingsClick = {
          navHostController.navigate(Screen.Settings.route)
        },
        onAccountClick = {
          navHostController.navigate(Screen.SigningScreen.route) // Navigate to InitialLoginScreen when Account is clicked
        },
        onFABClick = {
          navHostController.navigate(Screen.AddPost.route)
        }
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
  }
}

