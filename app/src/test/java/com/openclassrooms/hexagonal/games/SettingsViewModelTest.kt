package com.openclassrooms.hexagonal.games

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.screen.settings.SettingsViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val sharedPreferences: SharedPreferences = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Override the Main dispatcher for tests

        // Mock SharedPreferences and its Editor
        val editor: SharedPreferences.Editor = mockk()
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.apply() } returns Unit

        viewModel = SettingsViewModel(sharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher after tests
    }

    @Test
    fun `enableNotifications stores true in SharedPreferences`() = runTest(testDispatcher) {
        // Given
        val editor: SharedPreferences.Editor = mockk()
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.apply() } returns Unit

        // When
        viewModel.enableNotifications()

        // Then
        coVerifySequence {
            sharedPreferences.edit()
            editor.putBoolean("notifications_enabled", true)
            editor.apply()
        }
    }

    @Test
    fun `disableNotifications stores false in SharedPreferences`() = runTest(testDispatcher) {
        // Given
        val editor: SharedPreferences.Editor = mockk()
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.apply() } returns Unit

        // When
        viewModel.disableNotifications()

        // Then
        coVerifySequence {
            sharedPreferences.edit()
            editor.putBoolean("notifications_enabled", false)
            editor.apply()
        }
    }

    @Test
    fun `areNotificationsEnabled returns true when enabled`() = runTest(testDispatcher) {
        // Given
        every { sharedPreferences.getBoolean("notifications_enabled", any()) } returns true

        // When
        val result = viewModel.areNotificationsEnabled()

        // Then
        assert(result)
    }

    @Test
    fun `areNotificationsEnabled returns false when disabled`() = runTest(testDispatcher) {
        // Given
        every { sharedPreferences.getBoolean("notifications_enabled", any()) } returns false

        // When
        val result = viewModel.areNotificationsEnabled()

        // Then
        assert(!result)
    }

    @Test
    fun `areNotificationsEnabled returns false when key doesn't exist`() = runTest(testDispatcher) {
        // Given
        // Here we're not specifying a return value, so it will default to the second parameter of getBoolean
        every { sharedPreferences.getBoolean("notifications_enabled", any()) } answers {
            secondArg<Boolean>()
        }

        // When
        val result = viewModel.areNotificationsEnabled()

        // Then
        assert(!result)
    }
}