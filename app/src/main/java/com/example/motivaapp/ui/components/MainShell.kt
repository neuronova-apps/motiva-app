package com.example.motivaapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.repository.QuoteRepository
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.ui.explore.ExploreScreen
import com.example.motivaapp.ui.explore.CategoryScreen
import com.example.motivaapp.ui.home.HomeScreen
import com.example.motivaapp.ui.profile.ProfileScreen
import com.example.motivaapp.ui.saved.SavedScreen
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaColors

enum class MainDestination(val label: String) {
    TODAY("Hoy"),
    EXPLORE("Explorar"),
    SAVED("Guardados"),
    PROFILE("Perfil"),
}

@Composable
fun MainShell(
    selectedDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    quoteRepository: QuoteRepository,
    preferences: UserPreferences,
    categories: List<String>,
    onToggleFavorite: (String) -> Unit,
    onCategoryOpened: (String) -> Unit,
    onSaveDisplayName: (String?) -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler(enabled = selectedCategory != null) { selectedCategory = null }
    selectedCategory?.let { category ->
        CategoryScreen(
            category = category,
            repository = quoteRepository,
            favoriteQuoteIds = preferences.favoriteQuoteIds,
            onToggleFavorite = onToggleFavorite,
            onBack = { selectedCategory = null },
            modifier = modifier,
        )
        return
    }

    BackHandler(enabled = selectedDestination != MainDestination.TODAY) {
        onDestinationSelected(MainDestination.TODAY)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.motivaColors.appBackground,
        contentColor = MaterialTheme.motivaColors.primaryText,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            val highContrast = LocalHighContrastEnabled.current
            Column {
                if (highContrast) {
                    HorizontalDivider(
                        thickness = 3.dp,
                        color = MaterialTheme.motivaColors.strongBorder,
                    )
                }
                NavigationBar(containerColor = MaterialTheme.motivaColors.navigationBackground) {
                    MainDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = destination == selectedDestination,
                            onClick = { onDestinationSelected(destination) },
                            icon = {
                                Icon(
                                    imageVector = destination.icon(),
                                    contentDescription = null,
                                )
                            },
                            label = { Text(destination.label) },
                            colors = if (highContrast) {
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.motivaColors.selectedContent,
                                    selectedTextColor = MaterialTheme.motivaColors.primaryText,
                                    indicatorColor = MaterialTheme.motivaColors.selectedContainer,
                                    unselectedIconColor = MaterialTheme.motivaColors.unselectedContent,
                                    unselectedTextColor = MaterialTheme.motivaColors.unselectedContent,
                                )
                            } else {
                                NavigationBarItemDefaults.colors()
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (selectedDestination) {
            MainDestination.TODAY -> HomeScreen(
                repository = quoteRepository,
                favoriteQuoteIds = preferences.favoriteQuoteIds,
                onToggleFavorite = onToggleFavorite,
                onOpenProfile = { onDestinationSelected(MainDestination.PROFILE) },
                modifier = contentModifier,
            )
            MainDestination.EXPLORE -> ExploreScreen(
                categories = categories,
                onOpenCategory = { category ->
                    onCategoryOpened(category)
                    selectedCategory = category
                },
                modifier = contentModifier,
            )
            MainDestination.SAVED -> SavedScreen(
                repository = quoteRepository,
                favoriteQuoteIds = preferences.favoriteQuoteIds,
                onToggleFavorite = onToggleFavorite,
                modifier = contentModifier,
            )
            MainDestination.PROFILE -> ProfileScreen(
                preferences = preferences,
                availableCategories = categories.toSet(),
                onSaveDisplayName = onSaveDisplayName,
                onOpenAchievements = onOpenAchievements,
                onOpenSettings = onOpenSettings,
                modifier = contentModifier,
            )
        }
    }
}

private fun MainDestination.icon(): ImageVector = when (this) {
    MainDestination.TODAY -> Icons.Default.Home
    MainDestination.EXPLORE -> Icons.Default.Search
    MainDestination.SAVED -> Icons.Default.Favorite
    MainDestination.PROFILE -> Icons.Default.Person
}
