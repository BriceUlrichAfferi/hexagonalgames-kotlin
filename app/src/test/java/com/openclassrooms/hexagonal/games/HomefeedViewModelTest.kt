package com.openclassrooms.hexagonal.games

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*

@ExperimentalCoroutinesApi
class HomefeedViewModelTest {

    private lateinit var viewModel: HomefeedViewModel
    private val postRepository: PostRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Mock the behavior of PostRepository
        coEvery { postRepository.posts } returns flowOf(emptyList())
        coEvery { postRepository.getPostById(any()) } returns null
        coEvery { postRepository.getCommentsForPost(any()) } returns emptyList()
        coEvery { postRepository.getPostsRealtime() } returns flowOf(emptyList())

        // Mock Log class to avoid runtime exceptions
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.d(any(), any(), any()) } returns 0 // Overload for when an exception is logged

        Dispatchers.setMain(testDispatcher) // Override the Main dispatcher for tests
        viewModel = HomefeedViewModel(postRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher after tests
        unmockkStatic(android.util.Log::class) // Unmock Log after tests
    }

    // Existing tests...

    @Test
    fun `initialization collects posts and sets error when empty with real-time updates`() = runTest {
        // Given (Already set in setup)

        // When
        // Initialization happens in the constructor, so no action here

        // Then
        advanceUntilIdle()
        assert(viewModel.posts.value == emptyList<Post>())
        assert(viewModel.error.value == "no_publication")
    }

    @Test
    fun `initialization collects posts and clears error with real-time updates`() = runTest {
        // Given
        val posts = listOf(Post("id1", "author1", "content1"))
        coEvery { postRepository.posts } returns flowOf(posts)
        coEvery { postRepository.getPostsRealtime() } returns flowOf(posts)

        // When
        viewModel = HomefeedViewModel(postRepository)

        // Wait for all coroutines to finish
        advanceUntilIdle()

        // Then
        assert(viewModel.posts.value == posts) { "Expected posts ${posts}, but was ${viewModel.posts.value}" }
        assert(viewModel.error.value == null) { "Error was not cleared, it was ${viewModel.error.value}" }
    }

    @Test
    fun `getPostDetailsRealtime updates post details when available`() = runTest {
        // Given
        val postId = "somePostId"
        val post = Post(postId, "author", "content")
        coEvery { postRepository.getPostsRealtime() } returns flowOf(listOf(post))

        // When
        viewModel.getPostDetailsRealtime(postId)
        advanceUntilIdle()

        // Then
        assert(viewModel.postDetails.value == post)
    }

    @Test
    fun `getPostDetailsRealtime sets null when no posts are available`() = runTest {
        // Given
        val postId = "somePostId"
        coEvery { postRepository.getPostsRealtime() } returns flowOf(emptyList())

        // When
        viewModel.getPostDetailsRealtime(postId)
        advanceUntilIdle()

        // Then
        assert(viewModel.postDetails.value == null)
    }
}