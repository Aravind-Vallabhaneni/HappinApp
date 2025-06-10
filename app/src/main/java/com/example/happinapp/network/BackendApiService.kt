package com.example.happinapp.network


import com.example.happinapp.data.ApiResponse
import com.example.happinapp.data.MovieDetailResponse
import com.example.happinapp.data.MoviesByLocationResponse
import com.example.happinapp.data.UserProfile
import com.example.happinapp.data.UserRegistrationRequest
import okhttp3.ResponseBody // Import for handling generic success response
import retrofit2.Response // Import Retrofit's Response class
import retrofit2.http.Body // Annotation for request body
import retrofit2.http.GET
import retrofit2.http.POST // Annotation for POST HTTP method
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendApiService {
    // Defines a POST request to the "users/register" endpoint
    // @Body indicates that the UserRegistrationRequest object will be sent as the JSON request body
    // suspend means it's a suspend function, to be called from a coroutine
    // Response<ResponseBody> indicates we expect a network response, and on success,
    // we don't care about a specific JSON body from the backend (just the HTTP status, e.g., 200 OK)
    @POST("auth/register")
    suspend fun registerUser(@Body request: UserRegistrationRequest): Response<ResponseBody>

    @GET("user/profile/{uid}")
    suspend fun getUserProfile(@Path("uid") uid: String): Response<UserProfile>

    @GET("movies")
    suspend fun getMoviesByLocation(
        @Query("location") location: String
    ): Response<MoviesByLocationResponse>

    //  FUNCTION: Fetches detailed information for a specific movie.
    @GET("movies/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: String
    ): Response<MovieDetailResponse> // Uses the MovieDetailResponse wrapper

    //  FUNCTION: Fetches the trailer information for a specific movie.
    @GET("movies/{movieId}/trailer")
    suspend fun getMovieTrailer(
        @Path("movieId") movieId: String
    ): Response<ApiResponse> // Uses the generic ApiResponse
}