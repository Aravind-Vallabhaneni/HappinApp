package com.example.happinapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happinapp.data.UserProfile
import com.example.happinapp.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// This sealed interface represents the different states the Profile UI can be in.
sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val userProfile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()
    private val auth = FirebaseAuth.getInstance()

    // A private MutableStateFlow that will hold the current UI state.
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    // A public StateFlow that the UI will observe.
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Automatically fetch the profile when the ViewModel is created.
        fetchUserProfile()
    }

    /**
     * Fetches the user profile for the currently logged-in user.
     */
    private fun fetchUserProfile() {
        viewModelScope.launch {
            // Set the state to Loading before starting the network request.
            _uiState.value = ProfileUiState.Loading

            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.value = ProfileUiState.Error("User is not logged in.")
                return@launch
            }

            try {
                // Call the repository to get the profile data.
                val userProfile = profileRepository.getUserProfile(currentUser.uid)
                // If successful, update the state with the user profile data.
                _uiState.value = ProfileUiState.Success(userProfile)
            } catch (e: Exception) {
                // If an error occurs, update the state with the error message.
                _uiState.value = ProfileUiState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
}