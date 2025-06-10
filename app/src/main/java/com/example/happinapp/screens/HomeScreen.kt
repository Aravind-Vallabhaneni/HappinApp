package com.example.happinapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items // Keep this for LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
// Import for rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveable // NEW IMPORT
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.happinapp.data.MovieItem
import com.example.happinapp.navigation.Screen
import com.example.happinapp.ui.theme.HappinAppTheme
import com.example.happinapp.viewmodels.HomeViewModel
import com.example.happinapp.viewmodels.MoviesUiState


val ChipSelectedColor = Color(0xFFB39DDB)
val ChipUnselectedColor = Color.White.copy(alpha = 0.15f)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    loggedInUid: String?,
    navController: NavController,
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val location by homeViewModel.selectedLocation.collectAsState()
    val isInitialLocationCheckComplete by homeViewModel.isInitialLocationCheckComplete.collectAsState()

    // NEW: Flag to ensure we only attempt initial navigation to location selection once
    var hasAttemptedInitialLocationNav by rememberSaveable { mutableStateOf(false) }

    val moviesUiState by homeViewModel.moviesUiState.collectAsState()
    val availableGenres by remember { mutableStateOf(homeViewModel.availableGenres) }
    val selectedGenre by homeViewModel.selectedGenre.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    // MODIFIED: LaunchedEffect now also considers hasAttemptedInitialLocationNav
    LaunchedEffect(isInitialLocationCheckComplete, location, hasAttemptedInitialLocationNav) {
        if (isInitialLocationCheckComplete && location == null && !hasAttemptedInitialLocationNav) {
            navController.navigate(Screen.Location.route) {
                launchSingleTop = true
            }
            hasAttemptedInitialLocationNav = true // Set the flag after navigating
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(TopGradientColor, BottomGradientColor),
                    start = Offset(0f, 0f),
                    end = Offset(
                        0f,
                        0.6f * LocalDensity.current.density * LocalConfiguration.current.screenHeightDp
                    )
                )
            )
            .safeContentPadding()
    ) {
        if (!isInitialLocationCheckComplete || (location == null && !hasAttemptedInitialLocationNav)) {
            // Show loading if initial location check isn't complete OR
            // if location is null AND we haven't yet tried to navigate to location selection.
            // This prevents content from flashing before the potential navigation.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTextColor)
            }
        } else {
            // Fixed header content
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // --- Top Bar: Location and Profile ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = PrimaryTextColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.clickable { navController.navigate(Screen.Location.route) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = location ?: "Select Location",
                                color = PrimaryTextColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Change Location",
                                tint = PrimaryTextColor
                            )
                        }
                        Text(
                            text = "View all events in this city",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        navController.navigate(Screen.Profile.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = PrimaryTextColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Search Bar ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { homeViewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for anything...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryTextColor,
                        unfocusedTextColor = PrimaryTextColor,
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = PrimaryTextColor,
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Genre Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableGenres) { genre ->
                    FilterChip(
                        selected = (genre == selectedGenre),
                        onClick = { homeViewModel.selectGenre(genre) },
                        label = { Text(genre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChipSelectedColor,
                            selectedLabelColor = Color.Black,
                            selectedLeadingIconColor = Color.Black,
                            containerColor = ChipUnselectedColor,
                            labelColor = PrimaryTextColor,
                            iconColor = PrimaryTextColor
                        ),
                        leadingIcon = if (genre == selectedGenre) { {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                        } else { null }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Content Area - Movies or Loading/Error states
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = moviesUiState) {
                    is MoviesUiState.Loading -> {
                        if (isInitialLocationCheckComplete && location != null) { // Only show movie loading if location is set
                            CircularProgressIndicator(
                                color = PrimaryTextColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    is MoviesUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp).align(Alignment.Center)
                        )
                    }
                    is MoviesUiState.Success -> {
                        if (state.movies.isEmpty()) {
                            // Show "No movies found..." only if initial location check is complete
                            // and we are not in an initial loading state for location itself
                            if (isInitialLocationCheckComplete) {
                                Text(
                                    text = if (selectedGenre == "All" && searchQuery.isBlank()) "No movies found for this location."
                                    else if (searchQuery.isNotBlank()) "No movies found for '$searchQuery'."
                                    else "No movies found for '$selectedGenre'.",
                                    color = PrimaryTextColor,
                                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.movies) { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onClick = {
                                            navController.navigate(Screen.MovieDetail.createRoute(movie.movieId))
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreenCorrectedFlowFinal() {
    HappinAppTheme {
        HomeScreen(
            loggedInUid = "sample_uid",
            navController = rememberNavController(),
            onLogout = {}
        )
    }
}