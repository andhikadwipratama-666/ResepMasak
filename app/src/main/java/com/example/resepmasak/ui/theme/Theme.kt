package com.example.resepmasak.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val TerracottaPrimary = Color(0xFFD95D39)
private val OnTerracotta = Color(0xFFFFFFFF)
private val WarmAmberContainer = Color(0xFFFFE5DC)
private val OnWarmAmberContainer = Color(0xFF3B1205)

private val SageSecondary = Color(0xFF4F772D)
private val CreamBackground = Color(0xFFFFFBF7)
private val WarmSurface = Color(0xFFFFFFFF)
private val WarmSurfaceVariant = Color(0xFFF6EFE8)
private val InkDark = Color(0xFF1F1A17)
private val InkMuted = Color(0xFF6B5E55)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = OnTerracotta,
    primaryContainer = WarmAmberContainer,
    onPrimaryContainer = OnWarmAmberContainer,
    secondary = SageSecondary,
    onSecondary = Color.White,
    background = CreamBackground,
    onBackground = InkDark,
    surface = WarmSurface,
    onSurface = InkDark,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = InkMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = Color(0xFF2C0D04),
    primaryContainer = Color(0xFF5C2312),
    onPrimaryContainer = Color(0xFFFFE5DC),
    secondary = Color(0xFF90BE6D),
    onSecondary = Color(0xFF132607),
    background = Color(0xFF161311),
    onBackground = Color(0xFFF5EFEA),
    surface = Color(0xFF1F1B18),
    onSurface = Color(0xFFF5EFEA),
    surfaceVariant = Color(0xFF2C2622),
    onSurfaceVariant = Color(0xFFC7B9AE)
)

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)

@Composable
fun ResepMasakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}