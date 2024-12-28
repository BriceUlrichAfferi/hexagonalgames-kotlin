package com.openclassrooms.hexagonal.games.data.repository


interface SigninContract {

    // View interface: Defines the UI methods the Presenter can call
    interface View {
        fun showToast(message: String)
        fun navigateToHomeScreen()
        fun showEmailError(errorMessage: String)
        fun showPasswordError(errorMessage: String)
    }

    // Presenter interface: Defines the methods the View can call
    interface Presenter {
        fun onEmailChanged(email: String)
        fun onPasswordChanged(password: String)
        fun onSignInClicked(email: String, password: String)
        fun onRegisterClicked(email: String, password: String)
    }
}
