package com.example.projetoapprotas.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Cores primárias e secundárias
val PrimaryBlue = Color(0xFF1B2A41)
val PrimaryLightBlue = Color(0xFF5C7AEA)
val SecondaryGreen = Color(0xFF4CAF50)
val SecondaryRed = Color(0xFFE53935)

// Cores neutras e de fundo
val BackgroundLight = Color(0xFFF5F7FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF121212)
val TextSecondary = Color(0xFF616161)

// Outras cores de destaque
val AccentCyan = Color(0xFF00BCD4)
val PrimaryContainerLight = Color(0xFFD6E4FF)
val OnPrimaryContainerDark = Color(0xFF003366)


val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = AccentCyan,
    tertiary = SecondaryGreen,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = SecondaryRed,
    onError = Color.White
)