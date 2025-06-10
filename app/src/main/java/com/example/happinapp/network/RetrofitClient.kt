package com.example.happinapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // For logging network requests
import retrofit2.Retrofit // The main Retrofit class
import retrofit2.converter.gson.GsonConverterFactory // For converting Kotlin objects to JSON and vice-versa
import java.util.concurrent.TimeUnit // For setting timeouts

object RetrofitClient {
    // For Android Emulator connecting to localhost on the computer: "http://10.0.2.2:8080/"
    // For a physical Android device connecting to localhost on your computer:

    //this is device base url
    private const val BASE_URL = "http://192.168.1.25:8080/"

    //this is emulator base url
//    private const val BASE_URL = "http://10.0.2.2:8080/"


    // HttpLoggingInterceptor is used to log the HTTP requests and responses in Logcat.
    // This is INVALUABLE for debugging network issues.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs request headers, body, response headers, body.
    }

    // OkHttpClient is a powerful HTTP client that Retrofit uses internally.
    // Here, we add the logging interceptor and set timeouts.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add the logging interceptor
        .connectTimeout(30, TimeUnit.SECONDS) // Time to establish connection
        .readTimeout(30, TimeUnit.SECONDS)    // Time to read data from server
        .writeTimeout(30, TimeUnit.SECONDS)   // Time to write data to server
        .build()

    // This is the Retrofit instance, configured with the base URL, OkHttpClient, and JSON converter.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Set the base URL for all API calls using this Retrofit instance
            .client(okHttpClient) // Use the custom OkHttpClient (with logging, timeouts)
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson to handle JSON <-> Kotlin object conversion
            .build()
    }

    // This is the actual instance of your BackendApiService that your ViewModel will use.
    // It's lazily initialized, meaning it's created only when first accessed.
    val apiService: BackendApiService by lazy {
        retrofit.create(BackendApiService::class.java) // Create the API service implementation
    }
}