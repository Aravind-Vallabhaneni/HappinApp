package com.example.happinapp.repository

import com.example.happinapp.data.MovieItem
import com.example.happinapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository {
    private val apiService = RetrofitClient.apiService

    // NEW: A simple in-memory cache for the movie list.
    private var cachedMovies: List<MovieItem> = emptyList()

    /**
     * Fetches a list of movies, caches it, and then returns it.
     */
    suspend fun getMoviesByLocation(location: String): List<MovieItem> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getMoviesByLocation(location)
            if (response.isSuccessful) {
                val movies = response.body()?.movies ?: emptyList()
                // NEW: Store the fetched list in our cache variable
                cachedMovies = movies
                movies // Return the newly fetched list
            } else {
                throw Exception("Failed to fetch movies: ${response.code()} - ${response.message()}")
            }
        }
    }

    /**
     * NEW: A function to get the movies that were cached from the last fetch.
     * This does NOT make a network call.
     */
    fun getCachedMovies(): List<MovieItem> {
        return cachedMovies
    }
}