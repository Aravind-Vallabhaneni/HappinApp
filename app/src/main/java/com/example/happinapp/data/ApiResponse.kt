package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

/**
 * A generic API response class that matches the structure
 * used by some of the backend endpoints (e.g., for trailer URL, registration success/error).
 */
data class ApiResponse(
    @SerializedName("Code")
    val code: Int,

    @SerializedName("Message")
    val message: String? // Message can be nullable or absent
)