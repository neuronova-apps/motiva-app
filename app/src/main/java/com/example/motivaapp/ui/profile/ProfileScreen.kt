package com.example.motivaapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.DeviceLocalDate
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.achievementStatuses
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun ProfileScreen(
    preferences: UserPreferences,
    availableCategories: Set<String>,
    onSaveDisplayName: (String?) -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingName by remember { mutableStateOf(false) }

    if (editingName) {
        EditNameDialog(
            currentName = preferences.displayName,
            onDismiss = { editingName = false },
            onSave = { name ->
                onSaveDisplayName(name)
                editingName = false
            },
        )
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.motivaColors.appBackground) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() },
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.motivaColors.featuredCardBackground,
                        contentColor = MaterialTheme.motivaColors.featuredCardContent,
                    ),
                    border = motivaBorderStroke(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = preferences.displayName?.let { "Hola, $it" } ?: "Hola",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "Tu espacio personal en Motiva",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.motivaColors.secondaryText,
                            )
                        }
                        TextButton(onClick = { editingName = true }) {
                            Text("Editar nombre")
                        }
                    }
                }

                StreakCard(preferences)

                Text(
                    text = "Frases guardadas: ${preferences.favoriteCount}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                val unlocked = achievementStatuses(preferences, availableCategories).count {
                    it.unlocked
                }
                ProfileOption(
                    title = "Mis logros",
                    description = if (unlocked == 1) {
                        "1 logro alcanzado"
                    } else {
                        "$unlocked logros alcanzados"
                    },
                    onClick = onOpenAchievements,
                    leading = "✓",
                )
                ProfileOption(
                    title = "Configuración",
                    description = "Preferencias, accesibilidad, apariencia y acerca de",
                    onClick = onOpenSettings,
                    leadingIcon = true,
                )
            }
        }
    }
}

@Composable
private fun StreakCard(preferences: UserPreferences) {
    val today = DeviceLocalDate.today()
    val activeDays = remember(today, preferences.usageDates) {
        DeviceLocalDate.daysEndingAt(today, 7).map(preferences.usageDates::contains)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Tu constancia: ${dayCount(preferences.currentStreak)}. " +
                    "Mejor racha: ${dayCount(preferences.bestStreak)}. " +
                    "${dayCount(preferences.totalUsageDays)} totales de uso."
            },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.cardBackground,
            contentColor = MaterialTheme.motivaColors.primaryText,
        ),
        border = motivaBorderStroke(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Tu constancia", style = MaterialTheme.typography.titleMedium)
            Text(
                text = dayCount(preferences.currentStreak),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                activeDays.forEach { active ->
                    Box(
                        modifier = Modifier
                            .size(if (active) 14.dp else 12.dp)
                            .background(
                                color = if (active) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape,
                            ),
                    )
                }
            }
            Text(
                text = "Mejor racha: ${dayCount(preferences.bestStreak)}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "${dayCount(preferences.totalUsageDays)} totales de uso",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (preferences.currentStreak <= 1 && preferences.totalUsageDays > 1) {
                Text(
                    text = "Hoy puedes comenzar una nueva racha.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProfileOption(
    title: String,
    description: String,
    onClick: () -> Unit,
    leading: String? = null,
    leadingIcon: Boolean = false,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .semantics { role = Role.Button },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.cardBackground,
            contentColor = MaterialTheme.motivaColors.primaryText,
        ),
        border = motivaBorderStroke(),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                leadingIcon -> Icon(Icons.Default.Settings, contentDescription = null)
                leading != null -> Text(leading, style = MaterialTheme.typography.titleLarge)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EditNameDialog(
    currentName: String?,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit,
) {
    var name by remember(currentName) { mutableStateOf(currentName.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.motivaColors.cardBackground,
        tonalElevation = 0.dp,
        title = { Text("¿Cómo quieres que te llamemos?") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(40) },
                label = { Text("Nombre o apodo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(name.trim().takeIf(String::isNotEmpty)) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.motivaColors.primaryAction,
                    contentColor = MaterialTheme.motivaColors.primaryActionContent,
                ),
            ) { Text("Guardar") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onSave(null) }) {
                    Text(if (currentName == null) "Omitir" else "Quitar nombre")
                }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        },
    )
}

private fun dayCount(value: Int): String = "$value ${if (value == 1) "día" else "días"}"
