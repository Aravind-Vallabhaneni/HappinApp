package com.example.happinapp.repository

import com.example.happinapp.data.MovieDetail
// We don't need to import the Trailer data class here directly, as we'll return the URL String.
import com.example.happinapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log // For optional logging

class MovieDetailRepository {
    private val apiService = RetrofitClient.apiService

    /**
     * Fetches detailed information for a specific movie.
     * @param movieId The ID of the movie to fetch.
     * @return The MovieDetail object.
     * @throws Exception if the API call fails or data is malformed.
     */
    suspend fun getMovieDetail(movieId: String): MovieDetail {
        return withContext(Dispatchers.IO) {
            val response = apiService.getMovieDetail(movieId)
            if (response.isSuccessful) {
                // The MovieDetailResponse wraps the actual MovieDetail object in a 'movie' field.
                response.body()?.movie ?: throw Exception("Movie detail data in response is null")
            } else {
                throw Exception("Failed to fetch movie details: ${response.code()} - ${response.message()}")
            }
        }
    }

    /**
     * Fetches the trailer URL for a specific movie.
     * Your backend returns an ApiResponse where the URL is in the 'Message' field.
     * @param movieId The ID of the movie.
     * @return The trailer URL string, or null if not found or an error occurs.
     */
    suspend fun getMovieTrailerUrl(movieId: String): String? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getMovieTrailer(movieId)
            if (response.isSuccessful) {
                response.body()?.message // This 'message' field contains the trailer URL
            } else {
                // Log the error if you want, but return null for a missing trailer
                Log.e("MovieDetailRepository", "Failed to fetch trailer for $movieId: ${response.code()} - ${response.message()}")
                null
            }
        }
    }
}