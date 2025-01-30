package com.openclassrooms.hexagonal.games

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class PostRepositoryTest {

    // Mock dependencies
    private lateinit var repository: PostRepository

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var postsCollection: CollectionReference

    @Mock
    private lateinit var documentReference: DocumentReference

    @Mock
    private lateinit var querySnapshot: QuerySnapshot

    @Mock
    private lateinit var documentSnapshot: DocumentSnapshot

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = PostRepository(firestore)

        // Mock Firestore collection
        `when`(firestore.collection("posts")).thenReturn(postsCollection)
    }

    @Test
    fun `getPostById returns post when document exists`() = runTest {
        // Given a valid postId
        val postId = "12345"
        val expectedPost = Post(id = postId, title = "Sample Post", description = "This is a test post")

        // Mock Firestore document retrieval
        `when`(postsCollection.document(postId)).thenReturn(documentReference)
        `when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.toObject(Post::class.java)).thenReturn(expectedPost)

        // When retrieving a post
        val result = repository.getPostById(postId)

        // Then the correct post is returned
        assertEquals(expectedPost, result)
    }

    @Test
    fun `getPostById returns null when document does not exist`() = runTest {
        val postId = "nonexistent"

        // Mock Firestore document retrieval
        `when`(postsCollection.document(postId)).thenReturn(documentReference)
        `when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        `when`(documentSnapshot.exists()).thenReturn(false)

        // When retrieving a post
        val result = repository.getPostById(postId)

        // Then null is returned
        assertNull(result)
    }

    @Test
    fun `fetchPosts emits correct list`() = runTest {
        val postList = listOf(
            Post(id = "1", title = "Post 1", description = "Description 1"),
            Post(id = "2", title = "Post 2", description = "Description 2")
        )

        // Mock Firestore query
        `when`(postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)).thenReturn(postsCollection)
        `when`(postsCollection.get()).thenReturn(Tasks.forResult(querySnapshot))

        // Mock documents returned by Firestore
        val mockDocument1 = mockDocumentSnapshot(postList[0])
        val mockDocument2 = mockDocumentSnapshot(postList[1])
        `when`(querySnapshot.documents).thenReturn(listOf(mockDocument1, mockDocument2))

        // When collecting Flow results
        val result = repository.posts.first()

        // Then it should emit the correct posts
        assertEquals(postList, result)
    }

    private fun mockDocumentSnapshot(post: Post): DocumentSnapshot {
        val mockDoc = mock(DocumentSnapshot::class.java)

        // Ensure to return the post object when toObject() is called
        `when`(mockDoc.toObject(Post::class.java)).thenReturn(post)
        `when`(mockDoc.id).thenReturn(post.id)

        // Return the mock document
        return mockDoc
    }


}

/**
 * A rule for using TestCoroutineDispatcher in JUnit tests.
 */
@ExperimentalCoroutinesApi
class MainCoroutineRule : TestWatcher(), TestCoroutineScope by TestCoroutineScope() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
        cleanupTestCoroutines()
    }
}
