package com.example.happinapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happinapp.data.MovieDetail
import com.example.happinapp.repository.MovieDetailRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed interface to represent the UI state for the MovieDetailScreen
sealed interface MovieDetailUiState {
    object Loading : MovieDetailUiState
    data class Success(val movieDetail: MovieDetail, val trailerUrl: String?) : MovieDetailUiState
    data class Error(val message: String) : MovieDetailUiState
    object Idle : MovieDetailUiState // Optional: For an initial state before loading starts
}

class MovieDetailViewModel : ViewModel() {

    private val repository = MovieDetailRepository()

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Idle)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    /**
     * Fetches both movie details and trailer URL for the given movieId.
     * This function should be called by the UI when it needs to load data.
     */
    fun fetchMovieData(movieId: String) {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            try {
                // Fetch movie details and trailer URL concurrently
                val movieDetailDeferred = async { repository.getMovieDetail(movieId) }
                val trailerUrlDeferred = async { repository.getMovieTrailerUrl(movieId) }

                // Await the results
                val movieDetail = movieDetailDeferred.await()
                val trailerUrl = trailerUrlDeferred.await()

                _uiState.value = MovieDetailUiState.Success(movieDetail, trailerUrl)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}