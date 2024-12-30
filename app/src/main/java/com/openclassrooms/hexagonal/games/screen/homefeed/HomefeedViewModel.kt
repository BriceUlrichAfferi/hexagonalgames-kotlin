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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class HomefeedViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

  private val _posts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
  val posts: StateFlow<List<Post>>
    get() = _posts

  // New state flow for comments
  private val _comments = MutableStateFlow<List<Comment>>(emptyList())
  val comments: StateFlow<List<Comment>> = _comments

  init {
    viewModelScope.launch {
      postRepository.posts.collect { postList ->
        Log.d("HomefeedViewModel", "Collecting posts: ${postList.size}")
        _posts.value = postList
      }
    }
  }

  fun getPostById(postId: String): Flow<Post?> = flow {
    val post = postRepository.getPostById(postId)
    emit(post)
  }.flowOn(Dispatchers.IO)

  // New function to fetch comments
// In HomefeedViewModel


  fun getCommentsForPost(postId: String) {
    viewModelScope.launch {
      val comments = postRepository.getCommentsForPost(postId)
      _comments.value = comments
    }
  }
}