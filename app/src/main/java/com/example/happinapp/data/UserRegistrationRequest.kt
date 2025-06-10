package com.example.happinapp.data

import com.google.gson.annotations.SerializedName

data class UserRegistrationRequest(

//    api request body
//    val uid: String,
//    val email: String,
//    val first_name: String,
//    val last_name: String,
//    val phone: String

    @SerializedName("uid") val firebaseUid: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone") val phone: String,

)
