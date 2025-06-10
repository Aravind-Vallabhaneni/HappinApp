package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class MoviesByLocationResponse(
    // Matching the structure from backend code
    @SerializedName("code")
    val code: Int,

    @SerializedName("movies")
    val movies: List<MovieItem>
)