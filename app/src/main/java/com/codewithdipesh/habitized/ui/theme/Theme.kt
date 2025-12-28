package com.codewithdipesh.habitized.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.codewithdipesh.habitized.R

private val DarkColors = darkColorScheme(
    background = Color(0xFF150F05),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFF5F5F5F),
    primary = Color(0xFFEEA445),
    onPrimary = Color.White,
    secondary = Color(0xFF1A1A1A) ,
    onSecondary = Color(0xFF5F5F5F),
    tertiary = Color(0xFF7C7878),
    onTertiary = Color(0xFFE0E0E0),
    outline = Color(0xFF2F2E2E),
    surfaceVariant = Color(0xFF2F2E2E),
    inverseOnSurface = Color.Black,
    surfaceContainerHigh = Color(0xFF4E4E4E),
    surfaceContainerLow = Color(0XFF7C7878),
    surfaceDim = Color.White,
    outlineVariant = Color.Black,
    scrim = Color(0XFF7C7878),
    surfaceBright = Color(0XFF1B1B1D),
    surfaceContainerLowest = Color(0xFF141313)
)

private val LightColors = lightColorScheme(
    background = Color(0xFFFDF4E9),
    surface = Color.White,
    onSurface = Color(0xFF828388),
    primary = Color(0xFFEEA445),
    onPrimary = Color.Black,
    secondary = Color.White ,
    onSecondary = Color.Black,
    tertiary = Color.White,
    onTertiary = Color.Black,
    outline = Color(0xFFD9D9D9),
    surfaceVariant = Color(0xFFD9D9D9),
    inverseOnSurface = Color.White,
    surfaceContainerHigh = Color(0xFFC7C7C7),
    surfaceContainerLow = Color(0xFFD9D9D9),
    surfaceDim = Color(0xFF7C7878),
    outlineVariant = Color.Black,
    scrim = Color(0XFF969696),
    surfaceBright = Color(0XFFFFFFFF),
    surfaceContainerLowest = Color(0XFFEAEAEA)

)

@Composable
fun HabitizedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if(darkTheme) DarkCustomColors else LightCustomColors
    val materialColors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    CompositionLocalProvider(LocalCustomColors provides colors){
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content
        )
    }
}