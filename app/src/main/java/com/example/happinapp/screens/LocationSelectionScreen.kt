package com.example.happinapp.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happinapp.R // NEW: Import R class to access resources
import com.example.happinapp.data.City
import com.example.happinapp.ui.theme.HappinAppTheme

// MODIFIED: Update the list to include the drawable resources
val supportedCities = listOf(
    City("Hyderabad", R.drawable.ic_hyderabad),
    City("Bengaluru", R.drawable.ic_bangalore),
    City("Chennai", R.drawable.ic_chennai)
)

@Composable
fun LocationSelectionScreen(
    onCitySelected: (City) -> Unit,
    onClose: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
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
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PrimaryTextColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Select your city",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(48.dp))

                // MODIFIED: Use a Row to lay out the cities horizontally
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                    supportedCities.forEach { city ->
                        // Using a Column to stack the Image and Text vertically
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onCitySelected(city) }
                        ) {
                            Image(
                                painter = painterResource(id = city.imageRes),
                                contentDescription = city.name,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Fit,
                                // This tints the image white
                                colorFilter = ColorFilter.tint(PrimaryTextColor)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = city.name,
                                color = PrimaryTextColor,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLocationSelectionScreen() {
    HappinAppTheme {
        LocationSelectionScreen(onCitySelected = {}, onClose = {})
    }
}