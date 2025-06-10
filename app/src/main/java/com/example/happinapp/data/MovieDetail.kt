package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class MovieDetail(
    @SerializedName("movieId")
    val movieId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("rating")
    val rating: Double, // Assuming rating is a number like 7.8

    @SerializedName("language")
    val language: String,

    @SerializedName("description")
    val description: String,

    //  backend uses 'duration' for this field
    @SerializedName("runtime")
    val runtime: String,

    @SerializedName("coverpicture") //  backend uses 'coverpicture_url'
    val coverPictureUrl: String,

    @SerializedName("genre")
    val genre: String, // Though not explicitly requested for display, it's in the response

    @SerializedName("format")
    val format: String
)

// Wrapper class to match backend's MovieDetailResponse structure
data class MovieDetailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("movie")
    val movie: MovieDetail
)