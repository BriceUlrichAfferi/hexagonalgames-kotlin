package com.openclassrooms.hexagonal.games

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.screen.ad.FormError
import com.openclassrooms.hexagonal.games.screen.ad.FormEvent
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AddViewModelTest {

    private lateinit var viewModel: AddViewModel
    private val postRepository: PostRepository = mockk()
    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseUser: FirebaseUser = mockk()
    private val firestore: FirebaseFirestore = mockk()
    private val documentReference: DocumentReference = mockk()
    private val documentSnapshot: DocumentSnapshot = mockk()

    @Before
    fun setup() {
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "user123"
        every { firebaseUser.displayName } returns "Test User"
        every { firestore.collection("users").document("user123") } returns documentReference

        // Mock Task for documentReference.get()
        val mockTask = mockk<Task<DocumentSnapshot>>()
        every { documentReference.get() } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<DocumentSnapshot>>()
            listener.onSuccess(documentSnapshot)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } answers {
            val listener = firstArg<OnFailureListener>()
            listener.onFailure(Exception("Mocked error"))
            mockTask
        }

        // Mock FirebaseFirestore and FirebaseApp
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns firestore
        mockkStatic(FirebaseApp::class)
        every { FirebaseApp.initializeApp(any()) } returns mockk()
        every { FirebaseApp.getInstance() } returns mockk()

        // Mock Log.e
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0 // Overload for when an exception is logged


        viewModel = AddViewModel(postRepository, firebaseAuth)
    }

    @Test
    fun `test action updates post`() {
        viewModel.onAction(FormEvent.TitleChanged("New Title"))
        assert(viewModel.post.value.title == "New Title")

        viewModel.onAction(FormEvent.DescriptionChanged("New Description"))
        assert(viewModel.post.value.description == "New Description")
    }

    @Test
    fun `test addPost when user exists`() = runTest {
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.getString("firstName") } returns "John"
        every { documentSnapshot.getString("lastName") } returns "Doe"
        justRun { postRepository.addPost(any(), any()) }

        viewModel.addPost()
        verify { postRepository.addPost(any(), any()) }
    }

}