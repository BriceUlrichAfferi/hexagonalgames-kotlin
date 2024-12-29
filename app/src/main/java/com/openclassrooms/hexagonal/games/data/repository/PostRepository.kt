package com.openclassrooms.hexagonal.games.data.repository


import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
  private val firestore: FirebaseFirestore // Firestore for database
) {

  /**
   * Retrieves a Flow object containing a list of Posts ordered by creation date
   * in descending order.
   *
   * @return Flow containing a list of Posts.
   */
  val posts: Flow<List<Post>> = flow {
    // Use Firestore to listen to the posts collection, ordered by timestamp
    val snapshot = firestore.collection("posts")
      .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp, most recent first
      .get()
      .await() // Await the result of the snapshot

    // Map the documents to Post objects and emit them
    val postList = snapshot.documents.mapNotNull { document ->
      document.toObject(Post::class.java)
    }

    emit(postList) // Emit the list of posts
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

  private fun savePostToFirestore(postId: String, post: Post) {
    firestore.collection("posts").document(postId).set(post)
  }
}
