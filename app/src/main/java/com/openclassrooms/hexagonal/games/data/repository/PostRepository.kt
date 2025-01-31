package com.openclassrooms.hexagonal.games.data.repository


import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
  private val firestore: FirebaseFirestore
) {

  /**
   * Retrieves a Flow object containing a list of Posts ordered by creation date
   * in descending order.
   *
   * @return Flow containing a list of Posts.
   */
  fun getPostsRealtime(): Flow<List<Post>> = callbackFlow {
    val listener = firestore.collection("posts")
      .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
      .addSnapshotListener { snapshot, exception ->
        if (exception != null) {
          Log.w("PostRepository", "Listen failed.", exception)
          close(exception)
          return@addSnapshotListener
        }

        val posts = snapshot?.documents?.mapNotNull { document ->
          document.toObject(Post::class.java)?.copy(id = document.id)
        } ?: emptyList()

        trySend(posts)
      }

    awaitClose { listener.remove() }
  }

  /**
   * Adds a new Post to Firestore, optionally uploading an image to Firebase Storage.
   *
   * @param post The Post object to be added.
   * @param imageUri The URI of the image to be uploaded.
   */
  fun addPost(post: Post, imageUri: Uri?) {
    val postId = UUID.randomUUID().toString()

    if (imageUri != null) {
      val storageRef = FirebaseStorage.getInstance().reference.child("posts/$postId.jpg")
      storageRef.putFile(imageUri).addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { uri ->
          val updatedPost = post.copy(photoUrl = uri.toString())
          savePostToFirestore(postId, updatedPost)
        }
      }
    } else {
      savePostToFirestore(postId, post)
    }
  }

  suspend fun getPostById(postId: String): Post? = firestore.collection("posts")
    .document(postId)
    .get()
    .await()
    .toObject(Post::class.java)

  suspend fun getCommentsForPost(postId: String): List<Comment> = firestore.collection("posts")
    .document(postId)
    .collection("comments")
    .orderBy("timestamp", Query.Direction.ASCENDING)
    .get()
    .await()
    .toObjects(Comment::class.java)

  private fun savePostToFirestore(postId: String, post: Post) {
    firestore.collection("posts").document(postId).set(post)
  }

  // This method listens for real-time updates for a specific post
  fun getPostDetailsRealtime(postId: String): Flow<Post?> = callbackFlow {
    val listener = firestore.collection("posts")
      .document(postId)
      .addSnapshotListener { snapshot, exception ->
        if (exception != null) {
          Log.w("PostRepository", "Listen failed for post $postId", exception)
          close(exception)
          return@addSnapshotListener
        }

        val post = snapshot?.toObject(Post::class.java)?.copy(id = snapshot.id)
        trySend(post)
      }

    awaitClose { listener.remove() }
  }
}