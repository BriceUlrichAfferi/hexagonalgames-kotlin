package com.openclassrooms.hexagonal.games.screen.ad

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor
  (private val postRepository: PostRepository,
   private val auth: FirebaseAuth
) : ViewModel() {

  /**
   * Internal mutable state flow representing the current post being edited.
   */
  private var _post = MutableStateFlow(
    Post(
      id = UUID.randomUUID().toString(),
      title = "",
      description = "",
      photoUrl = null,
      timestamp = System.currentTimeMillis(),
      author = null
    )
  )

  /**
   * Public state flow representing the current post being edited.
   * This is immutable for consumers.
   */
  val post: StateFlow<Post>
    get() = _post
  var imageUri: Uri? = null
  /**
   * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
   */
  val error = post.map {
    verifyPost()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = null,
  )

  /**
   * Handles form events like title and description changes.
   *
   * @param formEvent The form event to be processed.
   */
  fun onAction(formEvent: FormEvent) {
    when (formEvent) {
      is FormEvent.DescriptionChanged -> {
        _post.value = _post.value.copy(
          description = formEvent.description
        )
      }

      is FormEvent.TitleChanged -> {
        _post.value = _post.value.copy(
          title = formEvent.title
        )
      }
    }
  }

  /**
   * Attempts to add the current post to the repository after setting the author.
   *
   * TODO: Implement logic to retrieve the current user.
   */
  fun addPost() {
    val user = auth.currentUser
    if (user != null) {
      val firestore = FirebaseFirestore.getInstance()
      val userRef = firestore.collection("users").document(user.uid)

      userRef.get().addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
          val firstName = documentSnapshot.getString("firstName") ?: "Anonymous"
          val lastName = documentSnapshot.getString("lastName") ?: "User"

          val currentPost = _post.value.copy(
            author = User(user.uid, firstName, lastName)
          )
          postRepository.addPost(currentPost, imageUri)
        } else {
          // If the user document doesn't exist, fall back to auth data or default values
          Log.w("AddViewModel", "User profile data not found, using fallback.")
          val currentPost = _post.value.copy(
            author = User(user.uid, user.displayName ?: "Anonymous", "User")
          )
          postRepository.addPost(currentPost, imageUri)
        }
      }.addOnFailureListener { e ->
        Log.e("AddViewModel", "Error fetching user profile", e)
        // If fetching fails, fall back to auth data or default values
        val currentPost = _post.value.copy(
          author = User(user.uid, user.displayName ?: "Anonymous", "User")
        )
        postRepository.addPost(currentPost, imageUri)
      }
    } else {
      Log.e("AddViewModel", "No user is currently logged in.")
    }
  }



  /**
   * Verifies mandatory fields of the post
   * and returns a corresponding FormError if so.
   *
   * @return A FormError.TitleError if title is empty, null otherwise.
   */
  private fun verifyPost(): FormError? {
    return if (_post.value.title.isEmpty()) {
      FormError.TitleError
    } else {
      null
    }
  }


}
