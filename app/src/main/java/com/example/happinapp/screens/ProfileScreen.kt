package com.example.happinapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.* // MODIFIED: Using outlined icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happinapp.data.UserProfile
import com.example.happinapp.ui.theme.HappinAppTheme
import com.example.happinapp.viewmodels.ProfileUiState
import com.example.happinapp.viewmodels.ProfileViewModel

val ProfileBackgroundColor = Color(0xFF1A1A2E)
val CardBackgroundColor = Color(0xFF2C2C3A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ProfileBackgroundColor)
            )
        },
        containerColor = ProfileBackgroundColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is ProfileUiState.Error -> {
                    Text(text = state.message, color = Color.Red)
                }
                is ProfileUiState.Success -> {
                    ProfileContent(userProfile = state.userProfile)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(userProfile: UserProfile) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            ProfileHeader(userProfile = userProfile, onEditClick = {
                Toast.makeText(context, "Edit profile clicked!", Toast.LENGTH_SHORT).show()
            })
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            SectionHeader("History")
            InfoCard {
                // MODIFIED: Swapped ConfirmationNumber for a sleeker LocalActivity icon
                InfoRow(icon = Icons.Outlined.LocalActivity, text = "Movie tickets", onClick = {
                    Toast.makeText(context, "Movie tickets clicked!", Toast.LENGTH_SHORT).show()
                })
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionHeader("Support")
            InfoCard {
                Column {
                    // MODIFIED: Swapped HelpOutline for a more stylish Quiz icon
                    InfoRow(icon = Icons.Outlined.Quiz, text = "Frequently asked questions", onClick = {
                        Toast.makeText(context, "FAQ clicked!", Toast.LENGTH_SHORT).show()
                    })
                    Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 1.dp)
                    // MODIFIED: Swapped Feedback for a RateReview icon
                    InfoRow(icon = Icons.Outlined.RateReview, text = "Share feedback", onClick = {
                        Toast.makeText(context, "Feedback clicked!", Toast.LENGTH_SHORT).show()
                    })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            InfoCard {
                // MODIFIED: Swapped the filled Logout for the outlined version
                InfoRow(icon = Icons.Outlined.Logout, text = "Logout", onClick = {
                    Toast.makeText(context, "Logout clicked!", Toast.LENGTH_SHORT).show()
                })
            }
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            AppFooter()
        }
    }
}

// The helper composables (ProfileHeader, SectionHeader, etc.) have not changed.
// ...

@Composable
fun ProfileHeader(userProfile: UserProfile, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CardBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${userProfile.firstName} ${userProfile.lastName}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = userProfile.phone, color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.Gray)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun InfoCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor)
    ) {
        content()
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = Color.LightGray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, modifier = Modifier.weight(1f))
    }
}

@Composable
fun AppFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Happin", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("AV & LC", color = Color.DarkGray, fontSize = 12.sp)
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
fun PreviewProfileContentWithNewIcons() {
    HappinAppTheme {
        ProfileContent(
            userProfile = UserProfile(
                email = "test@example.com",
                firstName = "Aravind",
                lastName = "Vallabhaneni",
                phone = "7330963842"
            )
        )
    }
}