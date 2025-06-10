package com.example.happinapp.repository

import com.example.happinapp.data.UserProfile
import com.example.happinapp.network.RetrofitClient

class ProfileRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Fetches the user profile from the backend.
     * @param uid The Firebase UID of the user.
     * @return The UserProfile data on success.
     * @throws Exception on failure or if the response body is null.
     */
    suspend fun getUserProfile(uid: String): UserProfile {
        val response = apiService.getUserProfile(uid)
        if (response.isSuccessful) {
            // If the API call is successful, return the profile data.
            // Throw an exception if the body is unexpectedly null.
            return response.body() ?: throw Exception("Profile data is null")
        } else {
            // If the API call fails (e.g., 404 Not Found, 500 Server Error),
            // throw an exception with the error code.
            throw Exception("Failed to fetch profile: ${response.code()}")
        }
    }
}