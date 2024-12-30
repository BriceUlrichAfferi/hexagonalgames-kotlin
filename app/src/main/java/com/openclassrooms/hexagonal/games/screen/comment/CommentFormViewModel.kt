package com.openclassrooms.hexagonal.games.screen.comment

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentFormViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Context
) : ViewModel() {

    // State for storing the list of comments
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    // State for storing the comment text
    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    // State for tracking submission status
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Function to fetch comments for a specific post
    fun fetchComments(postId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .orderBy("timestamp") // Sort by timestamp (ascending order)
                    .get()
                    .addOnSuccessListener { result ->
                        val fetchedComments = result.map { document ->
                            document.toObject(Comment::class.java)
                        }
                        _comments.value = fetchedComments
                    }
                    .addOnFailureListener {
                        _errorMessage.value = "Failed to fetch comments."
                    }
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred: ${e.message}"
            }
        }
    }

    // Function to update the comment text
    fun updateCommentText(newText: String) {
        _commentText.value = newText
    }

    // Function to add a new comment
    fun addComment(postId: String, commentText: String) {
        updateCommentText(commentText)
        submitComment(postId)
    }

    // Function to submit the comment to Firestore
    private fun submitComment(postId: String) {
        val comment = _commentText.value.trim()
        if (comment.isEmpty()) {
            _errorMessage.value = "Comment cannot be empty."
            return
        }

        viewModelScope.launch {
            try {
                _isSubmitting.value = true
                _errorMessage.value = null

                val commentId = firestore.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document().id

                val newComment = Comment(
                    id = commentId,
                    postId = postId,
                    text = comment,
                    timestamp = System.currentTimeMillis()
                )

                firestore.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(commentId)
                    .set(newComment)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comment added successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        _errorMessage.value = "Failed to add comment."
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        _isSubmitting.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred: ${e.message}"
                _isSubmitting.value = false
            }
        }
    }
}
