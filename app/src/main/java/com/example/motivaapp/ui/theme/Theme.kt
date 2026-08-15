package com.example.motivaapp.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = DarkSage,
    primaryContainer = Color(0xFF294333),
    onPrimaryContainer = Color(0xFFDCE9DF),
    secondary = DarkBlueGrey,
    secondaryContainer = Color(0xFF293C46),
    onSecondaryContainer = Color(0xFFDCE7EC),
    tertiary = DarkLavender,
    tertiaryContainer = Color(0xFF40354A),
    onTertiaryContainer = Color(0xFFEAE2F1),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = Color(0xFF3B352A),
    onBackground = Ivory,
    onSurface = Ivory,
)

private val LightColorScheme = lightColorScheme(
    primary = Sage,
    onPrimary = Ivory,
    primaryContainer = SoftSage,
    onPrimaryContainer = Ink,
    secondary = BlueGrey,
    secondaryContainer = SoftBlueGrey,
    onSecondaryContainer = Ink,
    tertiary = Color(0xFF705C7D),
    tertiaryContainer = SoftLavender,
    onTertiaryContainer = Ink,
    background = Ivory,
    surface = Ivory,
    surfaceVariant = SoftSand,
    onBackground = Ink,
    onSurface = Ink,
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color.White,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF000000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F0F0),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF000000),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD9D9D9),
    onTertiaryContainer = Color.Black,
    background = HighContrastLightBackground,
    surface = HighContrastLightBackground,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE0E0E0),
    surfaceContainerHighest = Color(0xFFCCCCCC),
    surfaceVariant = Color(0xFFD9D9D9),
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF111111),
    outline = Color.Black,
    outlineVariant = Color(0xFF333333),
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF202020),
    onSecondaryContainer = Color.White,
    tertiary = Color.White,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF303030),
    onTertiaryContainer = Color.White,
    background = HighContrastDarkBackground,
    surface = Color(0xFF111111),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF121212),
    surfaceContainer = Color(0xFF1C1C1C),
    surfaceContainerHigh = Color(0xFF292929),
    surfaceContainerHighest = Color(0xFF383838),
    surfaceVariant = Color(0xFF242424),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFF2F2F2),
    outline = Color.White,
    outlineVariant = Color(0xFFDDDDDD),
)

val LocalHighContrastEnabled = staticCompositionLocalOf { false }

@Immutable
data class MotivaSemanticColors(
    val appBackground: Color,
    val cardBackground: Color,
    val featuredCardBackground: Color,
    val featuredCardContent: Color,
    val contextualCardBackground: Color,
    val contextualCardContent: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val strongBorder: Color,
    val subtleBorder: Color,
    val selectedContainer: Color,
    val selectedContent: Color,
    val unselectedContainer: Color,
    val unselectedContent: Color,
    val primaryAction: Color,
    val primaryActionContent: Color,
    val navigationBackground: Color,
)

private val LocalMotivaSemanticColors = staticCompositionLocalOf<MotivaSemanticColors> {
    error("MotivaSemanticColors must be provided by MotivaAppTheme")
}

val MaterialTheme.motivaColors: MotivaSemanticColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMotivaSemanticColors.current

fun motivaSemanticColors(
    darkTheme: Boolean,
    highContrast: Boolean,
): MotivaSemanticColors {
    val scheme = motivaColorScheme(darkTheme, highContrast)
    return if (highContrast) {
        MotivaSemanticColors(
            appBackground = scheme.background,
            cardBackground = scheme.surface,
            featuredCardBackground = scheme.surface,
            featuredCardContent = scheme.onSurface,
            contextualCardBackground = scheme.surface,
            contextualCardContent = scheme.onSurface,
            primaryText = scheme.onBackground,
            secondaryText = scheme.onSurfaceVariant,
            strongBorder = scheme.outline,
            subtleBorder = scheme.outlineVariant,
            selectedContainer = scheme.primary,
            selectedContent = scheme.onPrimary,
            unselectedContainer = if (darkTheme) Color.Black else Color.White,
            unselectedContent = if (darkTheme) Color.White else Color.Black,
            primaryAction = scheme.primary,
            primaryActionContent = scheme.onPrimary,
            navigationBackground = scheme.background,
        )
    } else {
        MotivaSemanticColors(
            appBackground = scheme.background,
            cardBackground = scheme.surfaceContainerLow,
            featuredCardBackground = scheme.primaryContainer,
            featuredCardContent = scheme.onPrimaryContainer,
            contextualCardBackground = scheme.secondaryContainer,
            contextualCardContent = scheme.onSecondaryContainer,
            primaryText = scheme.onBackground,
            secondaryText = scheme.onSurfaceVariant,
            strongBorder = scheme.outline,
            subtleBorder = scheme.outlineVariant,
            selectedContainer = scheme.primaryContainer,
            selectedContent = scheme.onPrimaryContainer,
            unselectedContainer = scheme.surfaceContainerLow,
            unselectedContent = scheme.onSurface,
            primaryAction = scheme.primary,
            primaryActionContent = scheme.onPrimary,
            navigationBackground = scheme.surfaceContainer,
        )
    }
}

@Composable
fun motivaBorderStroke(
    normalWidth: Dp = 1.dp,
    normalColor: Color? = null,
): BorderStroke = BorderStroke(
    width = if (LocalHighContrastEnabled.current) 3.dp else normalWidth,
    color = if (LocalHighContrastEnabled.current) {
        MaterialTheme.motivaColors.strongBorder
    } else {
        normalColor ?: MaterialTheme.colorScheme.outlineVariant
    },
)

@Composable
fun MotivaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    textScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val colorScheme = motivaColorScheme(darkTheme, highContrast)
    val semanticColors = motivaSemanticColors(darkTheme, highContrast)

    CompositionLocalProvider(
        LocalHighContrastEnabled provides highContrast,
        LocalMotivaSemanticColors provides semanticColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = motivaTypography(textScale),
            content = content,
        )
    }
}

fun motivaColorScheme(darkTheme: Boolean, highContrast: Boolean): ColorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColorScheme
        highContrast -> HighContrastLightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
