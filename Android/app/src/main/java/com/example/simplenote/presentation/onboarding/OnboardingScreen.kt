package com.example.simplenote.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.ui.theme.OnboardingPurple

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingPurple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar spacing
            Spacer(modifier = Modifier.height(60.dp))

            // Illustration
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.illustration),
                    contentDescription = "Onboarding Illustration",
                    modifier = Modifier.size(280.dp)
                )
            }

            Text(
                text = "Jot Down anything you want to achieve, today or in the future",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Default, // Inter font family
                    fontWeight = FontWeight.Bold, // 700
                    fontSize = 20.sp,
                    lineHeight = (20 * 1.4).sp, // 140% line height
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                ),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(300.dp))

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(width = 1.dp, color = OnboardingPurple),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = OnboardingPurple
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Let's Get Started",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = OnboardingPurple,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = OnboardingPurple,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

        }
    }
} 