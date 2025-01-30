package com.openclassrooms.hexagonal.games

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseUser

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var firebaseAuth: FirebaseAuth

    @Before
    fun setup() {
        firebaseAuth = mock()
        userRepository = UserRepository(firebaseAuth)
    }

    @Test
    fun `fetchSignInMethodsForEmail should return methods when successful`() = runBlockingTest {
        // Arrange
        val email = "test@example.com"
        val signInMethods = listOf("password", "google")

        // Create a mock SignInMethodQueryResult and set it up to return the sign-in methods
        val mockResult = mock<SignInMethodQueryResult>()
        `when`(mockResult.signInMethods).thenReturn(signInMethods)

        // Create a TaskCompletionSource to simulate the completion of the Task
        val taskCompletionSource = TaskCompletionSource<SignInMethodQueryResult>()
        val mockTask: Task<SignInMethodQueryResult> = taskCompletionSource.task

        // Set the task result
        taskCompletionSource.setResult(mockResult)

        // Mock FirebaseAuth to return the mock Task
        `when`(firebaseAuth.fetchSignInMethodsForEmail(email)).thenReturn(mockTask)

        // Act
        val result = userRepository.fetchSignInMethodsForEmail(email)

        // Assert
        Assert.assertEquals(signInMethods, result)
        verify(firebaseAuth).fetchSignInMethodsForEmail(email)
    }

    @Test
    fun `fetchSignInMethodsForEmail should return null when exception occurs`() = runBlockingTest {
        // Arrange
        val email = "test@example.com"

        // Simulate an exception being thrown when the method is called
        `when`(firebaseAuth.fetchSignInMethodsForEmail(email))
            .thenThrow(RuntimeException("Some error"))

        // Act
        val result = userRepository.fetchSignInMethodsForEmail(email)

        // Assert
        Assert.assertNull(result)
    }


    @Test
    fun `getCurrentUser should return current user`() {
        // Arrange
        val mockUser = mock<FirebaseUser>()
        `when`(firebaseAuth.currentUser).thenReturn(mockUser)

        // Act
        val result = userRepository.getCurrentUser()

        // Assert
        Assert.assertEquals(mockUser, result)
    }
}
