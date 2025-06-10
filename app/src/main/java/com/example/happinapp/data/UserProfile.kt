package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("phone")
    val phone: String
)