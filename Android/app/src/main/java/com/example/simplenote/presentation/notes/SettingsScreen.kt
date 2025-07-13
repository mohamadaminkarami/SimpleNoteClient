package com.example.simplenote.presentation.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.presentation.viewmodel.SettingsViewModel
import com.example.simplenote.util.ToastUtil
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
    }
    
    // Handle token refresh events
    LaunchedEffect(uiState.isRefreshingToken) {
        if (!uiState.isRefreshingToken && uiState.error == null) {
            // Token refresh completed successfully
            ToastUtil.showShort(context, "Token refreshed successfully")
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (error.contains("Token") || error.contains("refresh")) {
                ToastUtil.showShort(context, "Token refresh failed: $error")
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Top Bar


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6366F1)
                    )
                }
                Text(
                    text = "Back",
                    color = Color(0xFF6366F1),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(start = 2.dp)
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
        // User Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar (placeholder)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                if (uiState.isLoading) {
                    Text("Loading...", style = MaterialTheme.typography.bodyMedium)
                } else if (uiState.error != null) {
                    Text("Error loading user info", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                } else {
                    val fullName =
                        listOfNotNull(uiState.user?.firstName, uiState.user?.lastName)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                            .ifBlank { uiState.user?.username ?: "-" }
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with mail icon if available
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.user?.email ?: "-",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        // App Settings label
        Text(
            text = "APP SETTINGS",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
        )

        // Change Password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onChangePassword() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF6366F1)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // Refresh Token
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.refreshToken() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFF6366F1)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Refresh Token",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (uiState.isRefreshingToken) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF6366F1)
                )
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // Log Out
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog = true }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color(0xFFE53935)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Log Out",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out from the application?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Yes", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        shape = CircleShape,
                        border = ButtonDefaults.outlinedButtonBorder,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6366F1))
                    ) {
                        Text("Cancel", color = Color(0xFF6366F1))
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // App version
        Text(
            text = "Taha Notes v1.1",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
} 