package com.example.motivaapp.ui.accessibility

import com.example.motivaapp.data.preferences.AppearancePreference
import com.example.motivaapp.data.preferences.TextSizePreference
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.shouldUseDarkTheme
import com.example.motivaapp.ui.theme.motivaColorScheme
import com.example.motivaapp.ui.theme.motivaSemanticColors
import com.example.motivaapp.ui.theme.motivaTypography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityPreferencesTest {

    @Test
    fun `accessibility defaults follow system without high contrast`() {
        val preferences = UserPreferences.Neutral

        assertEquals(TextSizePreference.NORMAL, preferences.textSizePreference)
        assertFalse(preferences.highContrastEnabled)
        assertEquals(AppearancePreference.SYSTEM, preferences.appearancePreference)
    }

    @Test
    fun `text size options increase progressively`() {
        assertTrue(TextSizePreference.LARGE.scale > TextSizePreference.NORMAL.scale)
        assertTrue(TextSizePreference.VERY_LARGE.scale > TextSizePreference.LARGE.scale)
    }

    @Test
    fun `every material typography role scales immediately with the preference`() {
        val normal = motivaTypography(TextSizePreference.NORMAL.scale)
        val large = motivaTypography(TextSizePreference.LARGE.scale)
        val veryLarge = motivaTypography(TextSizePreference.VERY_LARGE.scale)
        val normalSizes = normal.allFontSizes()
        val largeSizes = large.allFontSizes()
        val veryLargeSizes = veryLarge.allFontSizes()

        normalSizes.indices.forEach { index ->
            assertTrue(largeSizes[index] > normalSizes[index])
            assertTrue(veryLargeSizes[index] > largeSizes[index])
        }
    }

    @Test
    fun `appearance modes resolve light dark and system correctly`() {
        assertFalse(shouldUseDarkTheme(AppearancePreference.LIGHT, systemInDarkTheme = true))
        assertTrue(shouldUseDarkTheme(AppearancePreference.DARK, systemInDarkTheme = false))
        assertFalse(shouldUseDarkTheme(AppearancePreference.SYSTEM, systemInDarkTheme = false))
        assertTrue(shouldUseDarkTheme(AppearancePreference.SYSTEM, systemInDarkTheme = true))
    }

    @Test
    fun `high contrast themes provide strong text and border contrast`() {
        listOf(false, true).forEach { darkTheme ->
            val scheme = motivaColorScheme(darkTheme, highContrast = true)
            val regular = motivaColorScheme(darkTheme, highContrast = false)

            assertTrue(scheme.onBackground.contrastAgainst(scheme.background) >= 7f)
            assertTrue(scheme.onSurface.contrastAgainst(scheme.surface) >= 7f)
            assertTrue(scheme.outline.contrastAgainst(scheme.background) >= 3f)
            assertTrue(scheme.background != regular.background)
            assertTrue(scheme.primaryContainer != regular.primaryContainer)
            assertTrue(scheme.secondaryContainer != regular.secondaryContainer)
        }
    }

    @Test
    fun `light and dark high contrast palettes use unmistakable opposite foundations`() {
        val light = motivaColorScheme(darkTheme = false, highContrast = true)
        val dark = motivaColorScheme(darkTheme = true, highContrast = true)

        assertEquals(Color.White, light.background)
        assertEquals(Color.Black, light.primary)
        assertEquals(Color.Black, dark.background)
        assertEquals(Color.White, dark.primary)
        assertTrue(light.outline.contrastAgainst(light.surface) >= 7f)
        assertTrue(dark.outline.contrastAgainst(dark.surface) >= 7f)
    }

    @Test
    fun `high contrast palettes match the diagnostic black white specification`() {
        val light = motivaColorScheme(darkTheme = false, highContrast = true)
        val dark = motivaColorScheme(darkTheme = true, highContrast = true)

        assertEquals(Color.White, light.background)
        assertEquals(Color.White, light.surface)
        assertEquals(Color.Black, light.onBackground)
        assertEquals(Color(0xFF111111), light.onSurfaceVariant)
        assertEquals(Color.Black, light.primary)
        assertEquals(Color.White, light.onPrimary)
        assertEquals(Color.Black, light.outline)

        assertEquals(Color.Black, dark.background)
        assertEquals(Color(0xFF111111), dark.surface)
        assertEquals(Color.White, dark.onBackground)
        assertEquals(Color(0xFFF2F2F2), dark.onSurfaceVariant)
        assertEquals(Color.White, dark.primary)
        assertEquals(Color.Black, dark.onPrimary)
        assertEquals(Color.White, dark.outline)
    }

    @Test
    fun `semantic tokens separate cards selections actions and navigation`() {
        val light = motivaSemanticColors(darkTheme = false, highContrast = true)
        val dark = motivaSemanticColors(darkTheme = true, highContrast = true)

        assertEquals(Color.White, light.cardBackground)
        assertEquals(Color.Black, light.selectedContainer)
        assertEquals(Color.White, light.selectedContent)
        assertEquals(Color.White, light.unselectedContainer)
        assertEquals(Color.Black, light.unselectedContent)
        assertEquals(Color.Black, light.primaryAction)
        assertEquals(Color.White, light.primaryActionContent)
        assertEquals(Color.White, light.navigationBackground)

        assertEquals(Color(0xFF111111), dark.cardBackground)
        assertEquals(Color.White, dark.selectedContainer)
        assertEquals(Color.Black, dark.selectedContent)
        assertEquals(Color.Black, dark.unselectedContainer)
        assertEquals(Color.White, dark.unselectedContent)
        assertEquals(Color.White, dark.primaryAction)
        assertEquals(Color.Black, dark.primaryActionContent)
        assertEquals(Color.Black, dark.navigationBackground)
    }

    @Test
    fun `normal and high contrast semantic foundations never share main values`() {
        listOf(false, true).forEach { darkTheme ->
            val normal = motivaSemanticColors(darkTheme, highContrast = false)
            val highContrast = motivaSemanticColors(darkTheme, highContrast = true)

            assertTrue(normal.appBackground != highContrast.appBackground)
            assertTrue(normal.featuredCardBackground != highContrast.featuredCardBackground)
            assertTrue(normal.selectedContainer != highContrast.selectedContainer)
            assertTrue(normal.primaryAction != highContrast.primaryAction)
            assertTrue(normal.navigationBackground != highContrast.navigationBackground)
        }
    }

    private fun androidx.compose.material3.Typography.allFontSizes() = listOf(
        displayLarge,
        displayMedium,
        displaySmall,
        headlineLarge,
        headlineMedium,
        headlineSmall,
        titleLarge,
        titleMedium,
        titleSmall,
        bodyLarge,
        bodyMedium,
        bodySmall,
        labelLarge,
        labelMedium,
        labelSmall,
    ).map { it.fontSize.value }

    private fun Color.contrastAgainst(background: Color): Float {
        val lighter = maxOf(luminance(), background.luminance())
        val darker = minOf(luminance(), background.luminance())
        return (lighter + 0.05f) / (darker + 0.05f)
    }
}
