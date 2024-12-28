package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.openclassrooms.hexagonal.games.trial.SignInRepository
import com.openclassrooms.hexagonal.games.trial.SignInContract
import com.openclassrooms.hexagonal.games.screen.account.EmailInputScreen
import com.openclassrooms.hexagonal.games.screen.account.NameSurnameInputScreen
import com.openclassrooms.hexagonal.games.screen.account.PasswordInputScreen
import com.openclassrooms.hexagonal.games.trial.SignInPresenter

/*@Composable
fun SigninScreen(
    navController: NavController,
    repository: SignInRepository,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }

    // State for each step
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    val presenter = remember {
        SignInPresenter(
            view = object : SignInContract.View {
                override fun showEmailValidationError(error: String?) {
                    emailError = error
                }

                override fun navigateToStep(step: Int) {
                    currentStep = step
                }

                override fun showSuccessMessage(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    onLoginSuccess() // Call onLoginSuccess when login is successful
                }

                override fun showErrorMessage(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
            repository = repository
        )
    }

    when (currentStep) {
        1 -> EmailInputScreen(
            email = email,
            emailError = emailError,
            onEmailChange = { newEmail ->
                email = newEmail
                presenter.onEmailEntered(newEmail) // This will now trigger validation on every change
            },
            onValidateEmail = { presenter.onValidateEmail() } // Add this line
        )
        2 -> NameSurnameInputScreen(
            name = name,
            surname = surname,
            onNameChange = { newName ->
                name = newName
                presenter.onNameChanged(newName)
            },
            onSurnameChange = { newSurname ->
                surname = newSurname
                presenter.onSurnameChanged(newSurname)
            },
            onNext = { presenter.onProceedToPassword() }
        )
        3 -> PasswordInputScreen(
            password = password,
            onPasswordChange = { newPassword ->
                password = newPassword
                presenter.onPasswordChanged(newPassword)
            },
            onLogin = { presenter.onPasswordEntered() }
        )
    }
}*/