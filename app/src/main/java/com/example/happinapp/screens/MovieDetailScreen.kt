package com.example.happinapp.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri // IMPORT FOR URI PARSING
import android.util.Log // IMPORT FOR LOGGING
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.happinapp.data.MovieDetail
import com.example.happinapp.ui.theme.HappinAppTheme
import com.example.happinapp.viewmodels.MovieDetailUiState
import com.example.happinapp.viewmodels.MovieDetailViewModel

// Color constants
val MovieDetailScreenBackground = Color.Black
val MovieDetailTextColor = Color.White
val MovieDetailSecondaryTextColor = Color(0xFFB0B0B0)
val MovieDetailAccentColor = Color(0xFF7A4FD9)

// Tag for our specific logs
const val TRAILER_DEBUG_TAG = "TrailerDebug"

// Updated Helper function to extract YouTube Video ID
fun extractYoutubeVideoId(youtubeUrl: String): String? {
    Log.d(TRAILER_DEBUG_TAG, "Original URL to extract ID from: $youtubeUrl")
    return try {
        val uri = Uri.parse(youtubeUrl)
        // Try to get 'v' parameter first (for standard watch?v= links)
        var videoId = uri.getQueryParameter("v")
        if (!videoId.isNullOrBlank()) {
            Log.d(TRAILER_DEBUG_TAG, "Extracted ID (from 'v' param): $videoId")
            return videoId
        }

        // Fallback for other URL structures like youtu.be/ID or /embed/ID or /v/ID
        // or your specific case https://www.youtube.com/embed/$youtubeVideoId?autoplay=1\&controls=1\&modestbranding=13
        val pathSegments = uri.pathSegments
        if (pathSegments.isNotEmpty()) {
            // The ID is usually the last segment for many short/embed URLs
            val potentialId = pathSegments.last()
            // Basic check for typical YouTube ID format (11 chars, alphanumeric, _, -)
            if (potentialId.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
                Log.d(TRAILER_DEBUG_TAG, "Extracted ID (from last path segment): $potentialId")
                return potentialId
            }
        }
        Log.e(TRAILER_DEBUG_TAG, "Could not extract a valid Video ID from URL: $youtubeUrl")
        null
    } catch (e: Exception) {
        Log.e(TRAILER_DEBUG_TAG, "Error parsing YouTube URL: $youtubeUrl", e)
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: String,
    onNavigateBack: () -> Unit,
    movieDetailViewModel: MovieDetailViewModel = viewModel()
) {
    val uiState by movieDetailViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showTrailerPlayer by remember { mutableStateOf(false) }
    var currentTrailerVideoId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = movieId) {
        if (uiState is MovieDetailUiState.Idle || (uiState as? MovieDetailUiState.Success)?.movieDetail?.movieId != movieId) {
            movieDetailViewModel.fetchMovieData(movieId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MovieDetailTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MovieDetailScreenBackground
                )
            )
        },
        bottomBar = {
            if (uiState is MovieDetailUiState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MovieDetailScreenBackground)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Buy Ticket clicked!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MovieDetailAccentColor)
                    ) {
                        Text("Buy Ticket", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MovieDetailScreenBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is MovieDetailUiState.Idle, is MovieDetailUiState.Loading -> {
                    CircularProgressIndicator(color = MovieDetailTextColor, modifier = Modifier.align(Alignment.Center))
                }
                is MovieDetailUiState.Success -> {
                    MovieDetailContent(
                        movieDetail = state.movieDetail,
                        trailerUrl = state.trailerUrl,
                        onWatchTrailerClicked = { trailerUrlString ->
                            Log.d(TRAILER_DEBUG_TAG, "Watch Trailer Clicked. URL: $trailerUrlString")
                            val videoId = extractYoutubeVideoId(trailerUrlString)
                            if (videoId != null) {
                                currentTrailerVideoId = videoId
                                showTrailerPlayer = true
                            } else {
                                Toast.makeText(context, "Could not play trailer: Invalid URL.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
                is MovieDetailUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = Color.Red,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp).align(Alignment.Center)
                    )
                }
            }
        }

        if (showTrailerPlayer && currentTrailerVideoId != null) {
            YoutubePlayerDialog(
                youtubeVideoId = currentTrailerVideoId!!,
                onDismiss = { showTrailerPlayer = false }
            )
        }
    }
}

@Composable
fun MovieDetailContent(
    movieDetail: MovieDetail,
    trailerUrl: String?,
    onWatchTrailerClicked: (String) -> Unit
) {
    val context = LocalContext.current // Get context for Toast if needed inside here
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movieDetail.title,
                        color = MovieDetailTextColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${movieDetail.format} | ${movieDetail.language} | ${movieDetail.runtime}",
                        color = MovieDetailSecondaryTextColor,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            if (!trailerUrl.isNullOrBlank()) {
                                onWatchTrailerClicked(trailerUrl)
                            } else {
                                Toast.makeText(context, "Trailer not available.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MovieDetailSecondaryTextColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Trailer",
                            tint = MovieDetailTextColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Watch trailer", color = MovieDetailTextColor)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                AsyncImage(
                    model = movieDetail.coverPictureUrl,
                    contentDescription = movieDetail.title,
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Synopsis",
                color = MovieDetailTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movieDetail.description,
                color = MovieDetailSecondaryTextColor,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Rating",
                color = MovieDetailTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${movieDetail.rating}/10",
                color = MovieDetailTextColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom button leeway
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubePlayerDialog(
    youtubeVideoId: String,
    onDismiss: () -> Unit
) {
    Log.d(TRAILER_DEBUG_TAG, "YoutubePlayerDialog trying to load video ID: $youtubeVideoId")
    val embedUrl = "https://www.youtube-nocookie.com/embed/$youtubeVideoId?autoplay=1&controls=1&modestbranding=1&fs=0&rel=0"
    Log.d(TRAILER_DEBUG_TAG, "Constructed Embed URL: $embedUrl")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close trailer",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // Video player takes full width of the column here
                color = Color.Black
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    Log.d(TRAILER_DEBUG_TAG, "WebView page finished loading: $url")
                                }
                            }
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false

                            // MODIFIED: WebView settings for better layout control
                            settings.loadWithOverviewMode = false // Set to false
                            settings.useWideViewPort = false     // Set to false

                            // MODIFIED: More robust HTML for full bleed iframe
                            val htmlData = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0'>
                                    <style>
                                        html, body {
                                            margin: 0;
                                            padding: 0;
                                            width: 100%;
                                            height: 100%;
                                            overflow: hidden; /* Important */
                                            background-color: black;
                                        }
                                        iframe {
                                            /* position: absolute; */ /* May not be needed if body/html are 100% */
                                            /* top: 0; */
                                            /* left: 0; */
                                            width: 100%;
                                            height: 100%;
                                            border: none; /* Remove iframe border */
                                        }
                                    </style>
                                </head>
                                <body>
                                    <iframe 
                                        src="$embedUrl" 
                                        allow='autoplay; encrypted-media; picture-in-picture' 
                                        allowfullscreen>
                                    </iframe>
                                </body>
                                </html>
                            """.trimIndent()
                            loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlData, "text/html", "UTF-8", null)
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieDetailScreenComplete() {
    HappinAppTheme {
        MovieDetailScreen(movieId = "m001", onNavigateBack = {})
    }
}