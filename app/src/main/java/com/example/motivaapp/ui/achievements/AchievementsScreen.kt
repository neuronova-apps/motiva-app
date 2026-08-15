package com.example.motivaapp.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.achievementStatuses
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun AchievementsScreen(
    preferences: UserPreferences,
    availableCategories: Set<String>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statuses = achievementStatuses(preferences, availableCategories)
    SecondaryScreenScaffold(
        title = "Mis logros",
        onBack = onBack,
        backContentDescription = "Volver a Perfil",
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(20.dp, 24.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = "Pequeños hitos de tu tiempo con Motiva. Sin puntos ni competencia.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                )
            }
            items(statuses, key = { it.achievement.name }) { status ->
                val highContrast = LocalHighContrastEnabled.current
                val stateLabel = when {
                    status.unlocked -> "Conseguido"
                    !status.achievement.available -> "Próximamente"
                    else -> "Bloqueado"
                }
                Card(
                    modifier = Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = if (highContrast || status.unlocked) 1f else 0.72f
                        }
                        .semantics {
                            contentDescription = "${status.achievement.title}. $stateLabel. " +
                                status.achievement.description
                        },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = if (highContrast && status.unlocked) {
                            MaterialTheme.motivaColors.selectedContainer
                        } else if (status.unlocked) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.motivaColors.cardBackground
                        },
                        contentColor = if (highContrast && status.unlocked) {
                            MaterialTheme.motivaColors.selectedContent
                        } else {
                            MaterialTheme.motivaColors.primaryText
                        },
                    ),
                    border = motivaBorderStroke(
                        normalWidth = if (status.unlocked) 2.dp else 1.dp,
                        normalColor = if (status.unlocked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = if (status.unlocked) "✓" else "○",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                status.achievement.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(status.achievement.description, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                stateLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
