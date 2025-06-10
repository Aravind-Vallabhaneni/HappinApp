package com.example.happinapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happinapp.data.MovieItem
import com.example.happinapp.repository.LocationRepository
import com.example.happinapp.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface MoviesUiState {
    object Loading : MoviesUiState
    data class Success(val movies: List<MovieItem>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application.applicationContext)
    private val movieRepository = MovieRepository()

    // Private mutable state for the location, its initial value is what the UI sees first.
    private val _selectedLocationInternal = MutableStateFlow<String?>(null)
    // Public immutable StateFlow for the UI to observe the actual location.
    val selectedLocation: StateFlow<String?> = _selectedLocationInternal.asStateFlow()

    // Flag to indicate if the very first emission from DataStore has been processed.
    private val _isInitialLocationCheckComplete = MutableStateFlow(false)
    val isInitialLocationCheckComplete: StateFlow<Boolean> = _isInitialLocationCheckComplete.asStateFlow()

    val availableGenres: List<String> = listOf("All", "Action", "Comedy", "Drama", "Sci-Fi", "Thriller", "Horror")

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _rawMoviesUiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)

    val moviesUiState: StateFlow<MoviesUiState> = combine(
        _rawMoviesUiState,
        _selectedGenre,
        _searchQuery
    ) { rawState, genre, query ->
        when (rawState) {
            is MoviesUiState.Success -> {
                val moviesToProcess = rawState.movies
                val finalFilteredList: List<MovieItem>

                if (query.isNotBlank()) {
                    finalFilteredList = moviesToProcess.filter { movie ->
                        movie.title.contains(query, ignoreCase = true)
                    }
                } else {
                    finalFilteredList = if (genre == "All") {
                        moviesToProcess
                    } else {
                        moviesToProcess.filter { movie ->
                            movie.genre.contains(genre, ignoreCase = true)
                        }
                    }
                }
                MoviesUiState.Success(finalFilteredList)
            }
            else -> rawState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = MoviesUiState.Loading
    )

    init {
        viewModelScope.launch {
            // Collect directly from the repository's flow
            locationRepository.getLocation
                .collect { locationFromDataStore ->
                    _selectedLocationInternal.value = locationFromDataStore // Update the location UI will see
                    _isInitialLocationCheckComplete.value = true         // Crucially, set this flag only *after* first emission

                    if (locationFromDataStore != null) {
                        fetchMoviesByLocation(locationFromDataStore)
                    } else {
                        // If location is null after DataStore check, moviesUiState reflects no movies for that (null) location.
                        // HomeScreen will handle navigation to LocationSelection based on this null and isInitialLocationCheckComplete.
                        _rawMoviesUiState.value = MoviesUiState.Success(emptyList())
                    }
                }
        }
    }

    fun saveLocation(location: String) {
        viewModelScope.launch {
            locationRepository.saveLocation(location)
            // The collect block in init will pick up the new location from DataStore
            // and update _selectedLocationInternal & _isInitialLocationCheckComplete (though it's already true)
            // and then call fetchMoviesByLocation if the new location is not null.
        }
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
        if (_searchQuery.value.isNotBlank()) {
            _searchQuery.value = ""
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            if (_selectedGenre.value != "All") {
                _selectedGenre.value = "All"
            }
        }
    }

    private fun fetchMoviesByLocation(location: String) {
        viewModelScope.launch {
            _rawMoviesUiState.value = MoviesUiState.Loading
            try {
                val movies = movieRepository.getMoviesByLocation(location)
                _rawMoviesUiState.value = MoviesUiState.Success(movies)
            } catch (e: Exception) {
                _rawMoviesUiState.value = MoviesUiState.Error(e.message ?: "Failed to load movies.")
            }
        }
    }
}