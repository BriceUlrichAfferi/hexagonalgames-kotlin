package com.openclassrooms.hexagonal.games.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager(private val activity: ComponentActivity) {

    private val signInResult =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleSignInResult(result)
        }

    fun signInWithFirebase(onSuccess: () -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the user is already signed in
        if (currentUser != null) {
            // User is already signed in, show a toast message
            Toast.makeText(activity, "you are already registered!", Toast.LENGTH_SHORT).show()
            onSuccess() // Proceed to next action after displaying the toast
        } else {
            // User is not signed in, initiate the sign-in process
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
                .build()

            signInResult.launch(signInIntent)
        }
    }

    private fun handleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                // Proceed with login success logic
                Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorMessage = result.data?.getStringExtra("error_message") ?: "Login failed"
            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}
