package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class Trailer(
    @SerializedName("url")
    val url: String
)

// backend's trailer endpoint wraps the URL in an ApiResponse
// The actual URL is in the "Message" field of that ApiResponse.
// We'll handle this specific structure in the repository.
