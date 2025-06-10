package com.example.happinapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Import for Coil
import com.example.happinapp.data.MovieItem
import com.example.happinapp.ui.theme.HappinAppTheme

val MovieCardBackgroundColor = Color(0xFF2C2C3A)
val MovieCardTextColor = Color.White
val MovieCardSecondaryTextColor = Color.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: MovieItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp) // You might want to make this fill cell width or be responsive later
            .padding(8.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MovieCardBackgroundColor)
    ) {
        Column {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(
                        RoundedCornerShape( // Using the specific RoundedCornerShape for top corners
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth()
                // To ensure this text section has a consistent height contribution,
                // we can give it a minimum height or a fixed height.
                // Let's first try minLines on Title. If still issues, we can set a fixed height here.
                // .height(70.dp) // Example: Fixed height for the text area
            ) {
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MovieCardTextColor,
                    minLines = 2, // MODIFIED: Ensures space for 2 lines is always reserved
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.genre,
                    fontSize = 12.sp,
                    color = MovieCardSecondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConsistentMovieCard() {
    HappinAppTheme {
        Row(Modifier.background(Color(0xFF1A1A2E)).padding(8.dp)) {
            MovieCard(
                movie = MovieItem(
                    movieId = "1",
                    title = "Short Title",
                    posterUrl = "",
                    genre = "Action"
                ),
                onClick = {}
            )
            MovieCard(
                movie = MovieItem(
                    movieId = "2",
                    title = "A Much Longer Movie Title That Will Definitely Wrap to Two Lines",
                    posterUrl = "",
                    genre = "Adventure, Sci-Fi, Drama"
                ),
                onClick = {}
            )
        }
    }
}