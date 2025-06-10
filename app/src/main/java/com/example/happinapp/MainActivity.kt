package com.example.happinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.happinapp.navigation.AppNavGraph
import com.example.happinapp.navigation.Screen
import com.example.happinapp.ui.theme.HappinAppTheme
import kotlinx.coroutines.delay

// --- Your existing imports ---
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth // NEW: Import FirebaseAuth


// Your existing constants
val TopGradientColor = Color(0xFF7A4FD9)
val BottomGradientColor = Color(0xFF1A1A2E)
val PrimaryTextColor = Color(0xFFFFFFFF)

const val TAG = "LaunchScreen"
val districtFontFamily = FontFamily.SansSerif

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HappinAppTheme {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    LaunchScreen(onAnimationFinished = { showSplashScreen = false })
                } else {
                    // MODIFIED: This entire 'else' block is updated for persistent login
                    val navController = rememberNavController()
                    val firebaseAuth = FirebaseAuth.getInstance() // NEW: Get FirebaseAuth instance
                    val currentUser = firebaseAuth.currentUser     // NEW: Get current user

                    // Determine the start destination based on login state
                    val startDestination = if (currentUser != null) {
                        // User is already logged in, go to Home
                        // The HomeScreen will then handle the location check
                        Screen.Home.createRoute(currentUser.uid)
                    } else {
                        // User is not logged in, go to Login/Signup
                        Screen.LoginSignup.route
                    }

                    // Pass the calculated startDestination to the AppNavGraph
                    AppNavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}

// Your LaunchScreen composable and its preview remain exactly the same as you provided.
@Composable
fun LaunchScreen(onAnimationFinished: () -> Unit) {
    var showContent by remember { mutableStateOf(false) }
    val postEntryScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        delay(100L)
        showContent = true

        delay(800L)

        postEntryScale.animateTo(1.15f, animationSpec = tween(durationMillis = 100))
        postEntryScale.animateTo(1f, animationSpec = tween(durationMillis = 200))

        delay(800L)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(TopGradientColor, BottomGradientColor),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 0.6f * LocalDensity.current.density * LocalConfiguration.current.screenHeightDp)
                )
            )
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 10 },
                animationSpec = tween(durationMillis = 1000)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 1000)
            ) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(durationMillis = 1000)
            ),
            exit = ExitTransition.None
        ) {
            Text(
                text = "Happin",
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryTextColor,
                fontFamily = districtFontFamily,
                letterSpacing = (-0.05).em,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = postEntryScale.value,
                        scaleY = postEntryScale.value
                    )
                    .padding(horizontal = 24.dp)
            )
        }

        if (LocalInspectionMode.current.not()) {
            val doodleSize = 35.dp
            val doodleAlpha = 0.4f

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_popcorn),
                contentDescription = "Popcorn Doodle",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 16.dp, y = 16.dp)
                    .size(doodleSize)
                    .alpha(doodleAlpha)
            )

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_ticket),
                contentDescription = "Ticket Doodle",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 30.dp)
                    .size(doodleSize)
                    .alpha(doodleAlpha)
            )

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_magic),
                contentDescription = "Magic Doodle",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-20).dp, y = (-200).dp)
                    .size(doodleSize + 5.dp)
                    .alpha(doodleAlpha + 0.1f)
            )

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_cinema),
                contentDescription = "Cinema Doodle",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp, y = (-50).dp)
                    .size(doodleSize)
                    .alpha(doodleAlpha)
            )

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_popcorn),
                contentDescription = "Popcorn Doodle 2",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 30.dp, y = (-120).dp)
                    .size(doodleSize + 10.dp)
                    .alpha(doodleAlpha + 0.2f)
            )

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_ticket),
                contentDescription = "Ticket Doodle 2",
                colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp, y = (-100).dp)
                    .size(doodleSize)
                    .alpha(doodleAlpha)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LaunchScreenPreview() {
    HappinAppTheme {
        LaunchScreen(onAnimationFinished = {})
    }
}