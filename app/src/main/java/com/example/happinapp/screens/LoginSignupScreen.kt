package com.example.happinapp.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happinapp.ui.theme.HappinAppTheme

import androidx.compose.ui.res.painterResource
import com.example.happinapp.R
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happinapp.viewmodels.AuthViewModel

val TopGradientColor = Color(0xFF7A4FD9)
val BottomGradientColor = Color(0xFF1A1A2E)

val PrimaryTextColor = Color(0xFFFFFFFF)
val SecondaryTextColor = Color(0xFFCCCCCC)
val InputFieldBackgroundColor = Color(0x33FFFFFF)

@Composable
fun LoginSignupScreen(
    onLoginSuccess: (String) -> Unit, // Callback for successful login, passes UID
    onSignUpSuccess: () -> Unit, // Callback for successful signup (not used for nav in this step)
    authViewModel: AuthViewModel = viewModel() // ViewModel instance
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val loginResult by authViewModel.loginResult.collectAsState() // Observe login state from ViewModel

    // Effect for handling login outcomes
    LaunchedEffect(loginResult) {
        when (loginResult) {
            is AuthViewModel.AuthResult.Success -> {
                val firebaseUid = (loginResult as AuthViewModel.AuthResult.Success).firebaseUid
                Toast.makeText(context, "Login Successful! UID: $firebaseUid", Toast.LENGTH_SHORT).show()
                onLoginSuccess(firebaseUid) // Navigate on success
                authViewModel.resetLoginResult() // Reset ViewModel state
            }
            is AuthViewModel.AuthResult.Error -> {
                Toast.makeText(context, (loginResult as AuthViewModel.AuthResult.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetLoginResult()
            }
            AuthViewModel.AuthResult.Loading -> {
                Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show()
            }
            AuthViewModel.AuthResult.Idle -> { /* Do nothing */ }
            // NEW LINE: Handle the SuccessNoUid case (which is for signup, not login, so we do nothing here)
            //AuthViewModel.AuthResult.SuccessNoUid -> { /* This state is for signup, no action needed for login screen */ }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
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
                .safeContentPadding()
        ) {
            // --- MOVIE DOODLES (Keep as is from your previous code) ---
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

            // Main UI content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Happin",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = PrimaryTextColor
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = SecondaryTextColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextColor,
                            unfocusedTextColor = PrimaryTextColor,
                            focusedContainerColor = InputFieldBackgroundColor,
                            unfocusedContainerColor = InputFieldBackgroundColor,
                            focusedBorderColor = PrimaryTextColor,
                            unfocusedBorderColor = PrimaryTextColor,
                            cursorColor = PrimaryTextColor,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = SecondaryTextColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextColor,
                            unfocusedTextColor = PrimaryTextColor,
                            focusedContainerColor = InputFieldBackgroundColor,
                            unfocusedContainerColor = InputFieldBackgroundColor,
                            focusedBorderColor = PrimaryTextColor,
                            unfocusedBorderColor = PrimaryTextColor,
                            cursorColor = PrimaryTextColor,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Button - Calls ViewModel's login function
                    OutlinedButton(
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
                            } else if (password.isBlank()) {
                                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
                            } else {
                                authViewModel.login(email, password) // Trigger login
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(width = 2.dp, color = PrimaryTextColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryTextColor,
                            disabledContentColor = PrimaryTextColor.copy(alpha = 0.5f)
                        ),
                        enabled = loginResult !is AuthViewModel.AuthResult.Loading // Disable button while loading
                    ) {
                        Text(
                            text = if (loginResult is AuthViewModel.AuthResult.Loading) "Logging in..." else "Log In",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Signup Button (Placeholder for now)
                    OutlinedButton(
                        onClick = {
                            onSignUpSuccess()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(width = 2.dp, color = PrimaryTextColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryTextColor
                        )
                    ) {
                        Text("Sign Up", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.weight(1f).heightIn(min = 40.dp))

                Text(
                    text = "Made with ❤️ in Hyderabad",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginSignupScreen() {
    HappinAppTheme {
        LoginSignupScreen(onLoginSuccess = {}, onSignUpSuccess = {})
    }
}