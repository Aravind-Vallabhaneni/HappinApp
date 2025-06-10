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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For ViewModel integration
import com.example.happinapp.ui.theme.HappinAppTheme
import com.example.happinapp.R // For drawable resources
import com.example.happinapp.viewmodels.AuthViewModel // Import AuthViewModel

// Re-using colors and gradient definitions for consistency



@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit, // Callback to navigate back to login on success
    onNavigateBack: () -> Unit, // Callback to navigate back if user cancels/goes back
    authViewModel: AuthViewModel = viewModel() // ViewModel instance
) {
    // State variables for input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current // Get context for Toast messages

    // Observe the signupResult StateFlow from the AuthViewModel
    val signupResult by authViewModel.signupResult.collectAsState()

    // LaunchedEffect to react to changes in signupResult
    LaunchedEffect(signupResult) {
        when (signupResult) {
            is AuthViewModel.AuthResult.Success -> {
                Toast.makeText(context, "Signup Successful! Please log in.", Toast.LENGTH_LONG).show()
                onSignupSuccess() // Trigger navigation back to Login screen
                authViewModel.resetSignupResult() // Reset ViewModel state
            }
            is AuthViewModel.AuthResult.Error -> {
                Toast.makeText(context, (signupResult as AuthViewModel.AuthResult.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetSignupResult()
            }
            AuthViewModel.AuthResult.Loading -> {
                Toast.makeText(context, "Registering...", Toast.LENGTH_SHORT).show()
            }
            AuthViewModel.AuthResult.Idle, is AuthViewModel.AuthResult.Success -> {
                // Do nothing for Idle or regular Success (Success is for login, not signup here)
            }
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
                        end = Offset(
                            0f,
                            0.6f * LocalDensity.current.density * LocalConfiguration.current.screenHeightDp
                        )
                    )
                )
                .safeContentPadding()
        ) {
            // --- MOVIE DOODLES (Copied from LoginSignupScreen for consistency) ---
            if (LocalInspectionMode.current.not()) {
                val doodleSize = 35.dp
                val doodleAlpha = 0.4f

                // Top Left
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

                // Top Right
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

                // Mid-Left (near title)
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

                // Mid-Right (near inputs)
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

                // Bottom Left
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

                // Bottom Right
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

                    // First Name Field
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name", color = SecondaryTextColor) },
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

                    // Last Name Field
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name", color = SecondaryTextColor) },
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

                    //Phone field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number", color = SecondaryTextColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                    // Sign Up Button - Calls ViewModel's signup function
                    OutlinedButton(
                        onClick = {
                            // Basic UI validation
                            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            } else {
                                authViewModel.signup(firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phone = phone,
                                    password = password) // Trigger signup
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(width = 2.dp, color = PrimaryTextColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryTextColor,
                            disabledContentColor = PrimaryTextColor // Keep color consistent when disabled
                        ),
                        enabled = signupResult !is AuthViewModel.AuthResult.Loading // Disable button while loading
                    ) {
                        Text(
                            text = if (signupResult is AuthViewModel.AuthResult.Loading) "Registering..." else "Sign Up",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login Button
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            "Already have an account? Log In",
                            color = PrimaryTextColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp))

                Text(
                    text = "Made with ❤️ in Hyderabad",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    HappinAppTheme {
        SignupScreen(onSignupSuccess = {}, onNavigateBack = {})
    }
}