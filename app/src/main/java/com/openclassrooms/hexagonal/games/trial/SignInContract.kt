package com.openclassrooms.hexagonal.games.trial


interface SignInContract {
    interface View {
        fun showEmailValidationError(error: String?)
        fun navigateToStep(step: Int)
        fun showSuccessMessage(message: String)
        fun showErrorMessage(message: String)
    }

    interface Presenter {
        fun onEmailEntered(email: String)
        fun onNameAndSurnameEntered(name: String, surname: String)
        fun onPasswordEntered(password: String)
    }
}
