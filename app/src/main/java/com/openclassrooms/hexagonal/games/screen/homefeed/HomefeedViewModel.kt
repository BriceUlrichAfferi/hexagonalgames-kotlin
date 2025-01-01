package com.openclassrooms.hexagonal.games.screen.homefeed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class HomefeedViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

  private val _posts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
  val posts: StateFlow<List<Post>> = _posts.asStateFlow()

  // New state flow for comments
  private val _comments = MutableStateFlow<List<Comment>>(emptyList())
  val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

  // Error state
  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error.asStateFlow()

  init {
    viewModelScope.launch {
      postRepository.posts.collect { postList ->
        Log.d("HomefeedViewModel", "Collecting posts: ${postList.size}")
        if (postList.isEmpty()) {
          _error.value = "no_publication"
        } else {
          _posts.value = postList
          _error.value = null // Clear any previous error if we have posts
        }
      }
    }
  }

  fun getPostById(postId: String): Flow<Post?> = flow {
    val post = postRepository.getPostById(postId)
    if (post == null) {
      _error.value = "no_publication"
    } else {
      emit(post)
      _error.value = null // Clear error if post found
    }
  }.flowOn(Dispatchers.IO)

  fun getCommentsForPost(postId: String) {
    viewModelScope.launch {
      try {
        val comments = postRepository.getCommentsForPost(postId)
        _comments.value = comments
        _error.value = null // Clear any previous error
      } catch (e: Exception) {
        // Handle network errors here
        if (e is java.net.UnknownHostException) {
          _error.value = "no_network"
        } else {
          _error.value = "unknown_error" // or any other specific error type
        }
      }
    }
  }

  // Function to clear error state
  fun clearError() {
    _error.value = null
  }
}