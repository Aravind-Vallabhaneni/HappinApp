// happinapp/app/src/main/java/com/example/happinapp/viewmodels/AuthViewModel.kt
package com.example.happinapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.happinapp.network.BackendApiService // NEW: Import BackendApiService
import com.example.happinapp.network.RetrofitClient // NEW: Import RetrofitClient
import com.example.happinapp.data.UserRegistrationRequest // NEW: Import UserRegistrationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel" // Tag for Logcat messages

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val backendApiService: BackendApiService = RetrofitClient.apiService // NEW: Get instance of your backend API service

    // StateFlow to hold the result of the login operation
    private val _loginResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val loginResult: StateFlow<AuthResult> = _loginResult

    // StateFlow to hold the result of the signup operation (Firebase + Backend)
    private val _signupResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val signupResult: StateFlow<AuthResult> = _signupResult

    /**
     * Sealed class to represent the possible outcomes of authentication operations.
     * Success now always includes the UID, which is critical for backend registration.
     */
    sealed class AuthResult {
        object Idle : AuthResult() // Initial state, or after a successful/failed operation is reset
        object Loading : AuthResult() // Operation is in progress (Firebase or Backend)
        data class Success(val firebaseUid: String) : AuthResult() // Operation completed successfully
        data class Error(val message: String) : AuthResult() // Operation failed
    }

    /**
     * Initiates the user login process with Firebase Authentication.
     */
    fun login(email: String, password: String) {
        _loginResult.value = AuthResult.Loading // Set state to Loading
        viewModelScope.launch { // Launch a coroutine in the ViewModel's scope
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await() // Await Firebase result
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    Log.d(TAG, "Firebase Login successful: UID = ${firebaseUser.uid}")
                    _loginResult.value = AuthResult.Success(firebaseUser.uid) // Update state to Success
                } else {
                    Log.e(TAG, "Login successful but Firebase user is null.")
                    _loginResult.value = AuthResult.Error("Login successful, but user data not found.")
                }
            } catch (e: Exception) {
                // Handle specific Firebase authentication exceptions
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "Invalid email or user does not exist."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid password."
                    else -> "Login failed: ${e.localizedMessage ?: "Unknown error"}"
                }
                Log.e(TAG, "Firebase Login failed: $errorMessage", e)
                _loginResult.value = AuthResult.Error(errorMessage) // Update state to Error
            }
        }
    }

    /**
     * Resets the `loginResult` state to `Idle`.
     */
    fun resetLoginResult() {
        _loginResult.value = AuthResult.Idle
    }

    /**
     * Initiates the user signup process with Firebase Authentication and then
     * registers the user with the custom backend API.
     *
     * @param firstName User's first name.
     * @param lastName User's last name.
     * @param email User's email address.
     * @param phone User's phone number.
     * @param password User's chosen password.
     */
    fun signup(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String
    ) {
        _signupResult.value = AuthResult.Loading // Set state to Loading (Firebase part)
        viewModelScope.launch { // Launch a coroutine
            try {
                // --- 1. Firebase User Creation ---
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    Log.d(TAG, "Firebase Signup successful: UID = ${firebaseUser.uid}")

                    // --- 2. Register User with Custom Backend API ---
                    // It's good practice to show loading while backend call is happening
                    _signupResult.value = AuthResult.Loading // Keep loading state for backend call

                    val requestBody = UserRegistrationRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone,
                        firebaseUid  = firebaseUser.uid
                    )

                    Log.d(TAG, "Attempting to register user with backend: $requestBody")

                    val backendResponse = backendApiService.registerUser(requestBody) // Make the backend API call

                    if (backendResponse.isSuccessful) {
                        Log.d(TAG, "Backend registration successful! Status Code: ${backendResponse.code()}")
                        // Both Firebase and Backend registration successful
                        _signupResult.value = AuthResult.Success(firebaseUser.uid)
                    } else {
                        val errorBody = backendResponse.errorBody()?.string() // Get error body string
                        val errorMessage = "Backend registration failed: HTTP ${backendResponse.code()} - ${errorBody ?: "Unknown backend error"}"
                        Log.e(TAG, errorMessage)

                        // --- CRITICAL: Handle backend registration failure ---
                        // If backend registration fails, you might want to delete the Firebase user
                        // to prevent inconsistent data (user exists in Firebase but not in your backend).
                        firebaseUser.delete().await()
                        Log.d(TAG, "Firebase user ${firebaseUser.uid} deleted due to backend registration failure.")

                        _signupResult.value = AuthResult.Error("Registration failed: $errorMessage")
                    }
                } else {
                    Log.e(TAG, "Firebase Signup successful but user object is null. Cannot proceed to backend registration.")
                    _signupResult.value = AuthResult.Error("Signup failed: Firebase user not created.")
                }
            } catch (e: Exception) {
                // Handle exceptions from either Firebase or the network call itself
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException -> "This email address is already in use by another account."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid password. Password must be at least 6 characters long."
                    // Add more specific network error handling here if needed, e.g., UnknownHostException
                    else -> "Signup failed: ${e.localizedMessage ?: "Unknown error"}"
                }
                Log.e(TAG, "Signup process failed: $errorMessage", e)
                _signupResult.value = AuthResult.Error(errorMessage)
            }
        }
    }

    /**
     * Resets the `signupResult` state to `Idle`.
     */
    fun resetSignupResult() {
        _signupResult.value = AuthResult.Idle
    }
}