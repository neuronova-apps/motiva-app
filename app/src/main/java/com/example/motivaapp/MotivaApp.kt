package com.example.motivaapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.compose.BackHandler
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.motivaapp.data.preferences.AppearancePreference
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.UserPreferencesRepository
import com.example.motivaapp.data.repository.QuoteRepository
import com.example.motivaapp.ui.accessibility.AccessibilityScreen
import com.example.motivaapp.ui.achievements.AchievementsScreen
import com.example.motivaapp.ui.about.AboutScreen
import com.example.motivaapp.ui.appearance.AppearanceScreen
import com.example.motivaapp.ui.components.MainDestination
import com.example.motivaapp.ui.components.MainShell
import com.example.motivaapp.ui.onboarding.OnboardingScreen
import com.example.motivaapp.ui.preferences.PreferencesScreen
import com.example.motivaapp.ui.settings.SettingsScreen
import com.example.motivaapp.ui.theme.MotivaAppTheme
import kotlinx.coroutines.launch

enum class AppDestination {
    ONBOARDING,
    HOME,
}

private enum class ProfilePage {
    SETTINGS,
    PREFERENCES,
    ACCESSIBILITY,
    APPEARANCE,
    ABOUT,
    ACHIEVEMENTS,
}

fun destinationFor(preferences: UserPreferences): AppDestination =
    if (preferences.onboardingCompleted) AppDestination.HOME else AppDestination.ONBOARDING

fun shouldUseDarkTheme(
    preference: AppearancePreference,
    systemInDarkTheme: Boolean,
): Boolean = when (preference) {
    AppearancePreference.SYSTEM -> systemInDarkTheme
    AppearancePreference.LIGHT -> false
    AppearancePreference.DARK -> true
}

@Composable
fun MotivaApp(
    quoteRepository: QuoteRepository,
    preferencesRepository: UserPreferencesRepository,
    modifier: Modifier = Modifier,
) {
    val storedPreferences by preferencesRepository.preferences.collectAsStateWithLifecycle(
        initialValue = null,
    )
    var pendingHighContrast by remember { mutableStateOf<Boolean?>(null) }
    var selectedDestinationName by rememberSaveable {
        mutableStateOf(MainDestination.TODAY.name)
    }
    var profilePageName by rememberSaveable { mutableStateOf<String?>(null) }
    var availableCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val systemInDarkTheme = isSystemInDarkTheme()

    BackHandler(enabled = profilePageName != null) {
        profilePageName = when (profilePageName) {
            ProfilePage.PREFERENCES.name,
            ProfilePage.ACCESSIBILITY.name,
            ProfilePage.APPEARANCE.name,
            ProfilePage.ABOUT.name,
            -> ProfilePage.SETTINGS.name
            else -> null
        }
    }

    LaunchedEffect(preferencesRepository) {
        preferencesRepository.recordDailyUsage()
    }
    LaunchedEffect(quoteRepository) {
        availableCategories = runCatching { quoteRepository.getCategories() }.getOrDefault(emptyList())
    }
    LaunchedEffect(quoteRepository, storedPreferences?.favoriteQuoteIds) {
        val favoriteIds = storedPreferences?.favoriteQuoteIds.orEmpty()
        if (favoriteIds.isNotEmpty()) {
            val existingIds = runCatching {
                quoteRepository.getExistingQuoteIds(favoriteIds)
            }.getOrNull()
            if (existingIds != null && existingIds != favoriteIds) {
                preferencesRepository.retainExistingFavorites(existingIds)
            }
        }
    }
    LaunchedEffect(availableCategories, storedPreferences?.exploredCategories) {
        val explored = storedPreferences?.exploredCategories.orEmpty()
        val existingCategories = availableCategories.toSet()
        if (existingCategories.isNotEmpty() && !existingCategories.containsAll(explored)) {
            preferencesRepository.retainExistingExploredCategories(existingCategories)
        }
    }
    LaunchedEffect(storedPreferences?.highContrastEnabled, pendingHighContrast) {
        if (pendingHighContrast == storedPreferences?.highContrastEnabled) {
            pendingHighContrast = null
        }
    }

    val currentPreferences = storedPreferences?.let { stored ->
        pendingHighContrast?.let { stored.copy(highContrastEnabled = it) } ?: stored
    }
    if (currentPreferences == null) {
        MotivaAppTheme(darkTheme = systemInDarkTheme) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val useDarkTheme = shouldUseDarkTheme(
        preference = currentPreferences.appearancePreference,
        systemInDarkTheme = systemInDarkTheme,
    )

    MotivaAppTheme(
        darkTheme = useDarkTheme,
        highContrast = currentPreferences.highContrastEnabled,
        textScale = currentPreferences.textSizePreference.scale,
    ) {
        when {
            destinationFor(currentPreferences) == AppDestination.ONBOARDING -> OnboardingScreen(
                onComplete = { completedPreferences ->
                    coroutineScope.launch {
                        preferencesRepository.update { stored ->
                            completedPreferences.copy(
                                displayName = stored.displayName,
                                firstUseDate = stored.firstUseDate,
                                lastAccessDate = stored.lastAccessDate,
                                currentStreak = stored.currentStreak,
                                bestStreak = stored.bestStreak,
                                totalUsageDays = stored.totalUsageDays,
                                usageDates = stored.usageDates,
                                favoriteQuoteIds = stored.favoriteQuoteIds,
                                exploredCategories = stored.exploredCategories,
                            )
                        }
                    }
                },
                onSkip = {
                    coroutineScope.launch { preferencesRepository.completeWithNeutralDefaults() }
                },
                modifier = modifier,
            )
            profilePageName == ProfilePage.SETTINGS.name -> SettingsScreen(
                onOpenPreferences = { profilePageName = ProfilePage.PREFERENCES.name },
                onOpenAccessibility = { profilePageName = ProfilePage.ACCESSIBILITY.name },
                onOpenAppearance = { profilePageName = ProfilePage.APPEARANCE.name },
                onOpenAbout = { profilePageName = ProfilePage.ABOUT.name },
                onBack = { profilePageName = null },
                modifier = modifier,
            )
            profilePageName == ProfilePage.PREFERENCES.name -> PreferencesScreen(
                currentPreferences = currentPreferences,
                onSave = { updatedPreferences ->
                    coroutineScope.launch {
                        preferencesRepository.save(updatedPreferences)
                        profilePageName = ProfilePage.SETTINGS.name
                    }
                },
                onCancel = { profilePageName = ProfilePage.SETTINGS.name },
                modifier = modifier,
            )
            profilePageName == ProfilePage.ACCESSIBILITY.name -> AccessibilityScreen(
                currentPreferences = currentPreferences,
                onTextSizeChange = { textSize ->
                    coroutineScope.launch {
                        preferencesRepository.update { stored ->
                            stored.copy(textSizePreference = textSize)
                        }
                    }
                },
                onHighContrastChange = { enabled ->
                    pendingHighContrast = enabled
                    coroutineScope.launch {
                        runCatching {
                            preferencesRepository.update { stored ->
                                stored.copy(highContrastEnabled = enabled)
                            }
                        }.onFailure { pendingHighContrast = null }
                    }
                },
                onBack = { profilePageName = ProfilePage.SETTINGS.name },
                modifier = modifier,
            )
            profilePageName == ProfilePage.APPEARANCE.name -> AppearanceScreen(
                currentAppearance = currentPreferences.appearancePreference,
                onAppearanceChange = { appearance ->
                    coroutineScope.launch {
                        preferencesRepository.update { stored ->
                            stored.copy(appearancePreference = appearance)
                        }
                    }
                },
                onBack = { profilePageName = ProfilePage.SETTINGS.name },
                modifier = modifier,
            )
            profilePageName == ProfilePage.ABOUT.name -> AboutScreen(
                onBack = { profilePageName = ProfilePage.SETTINGS.name },
                modifier = modifier,
            )
            profilePageName == ProfilePage.ACHIEVEMENTS.name -> AchievementsScreen(
                preferences = currentPreferences,
                availableCategories = availableCategories.toSet(),
                onBack = { profilePageName = null },
                modifier = modifier,
            )
            else -> MainShell(
                selectedDestination = MainDestination.valueOf(selectedDestinationName),
                onDestinationSelected = { destination ->
                    selectedDestinationName = destination.name
                },
                quoteRepository = quoteRepository,
                preferences = currentPreferences,
                categories = availableCategories,
                onToggleFavorite = { quoteId ->
                    coroutineScope.launch { preferencesRepository.toggleFavorite(quoteId) }
                },
                onCategoryOpened = { category ->
                    coroutineScope.launch {
                        preferencesRepository.recordExploredCategory(category)
                    }
                },
                onSaveDisplayName = { displayName ->
                    coroutineScope.launch {
                        preferencesRepository.update { stored ->
                            stored.copy(displayName = displayName?.trim()?.takeIf(String::isNotEmpty))
                        }
                    }
                },
                onOpenAchievements = { profilePageName = ProfilePage.ACHIEVEMENTS.name },
                onOpenSettings = { profilePageName = ProfilePage.SETTINGS.name },
                modifier = modifier,
            )
        }
    }
}
