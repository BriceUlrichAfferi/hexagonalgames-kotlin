package com.openclassrooms.hexagonal.games.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  data object Homefeed : Screen("homefeed")
  
  data object AddPost : Screen("addPost")
  
  data object Settings : Screen("settings")

  data object SigningScreen : Screen("signing")
  object InitialLoginScreen : Screen("initialLoginScreen")
  object AccountManagementScreen : Screen("managementScreen")

  object SigninScaffold : Screen("signinScaffold")
  object SignIn : Screen("signIn")

  object PostDetails : Screen("postDetails/{postId}", listOf(
    navArgument("postId") { type = NavType.StringType }
  )) {
    fun createRoute(postId: String) = "postDetails/$postId"
  }

  data object CommentFormScreen : Screen(
    route = "commentForm/{postId}",
    navArguments = listOf(
      navArgument("postId") { type = NavType.StringType }
    )
  ) {
    fun createRoute(postId: String) = "commentForm/$postId"
  }

}