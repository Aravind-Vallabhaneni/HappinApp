package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class MovieItem(
    @SerializedName("movieId")
    val movieId: String,

    // backend's MovieItem model uses 'movieName' which maps to 'title' in the DB.
    // The JSON key sent to the client will be 'movieName'.
    @SerializedName("movieName")
    val title: String,

    @SerializedName("posterUrl")
    val posterUrl: String,

    @SerializedName("genre")
    val genre: String
)