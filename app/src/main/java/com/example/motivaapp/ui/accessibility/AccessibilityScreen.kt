package com.example.motivaapp.ui.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.TextSizePreference
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun AccessibilityScreen(
    currentPreferences: UserPreferences,
    onTextSizeChange: (TextSizePreference) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var textSize by remember(currentPreferences.textSizePreference) {
        mutableStateOf(currentPreferences.textSizePreference)
    }
    var highContrast by remember(currentPreferences.highContrastEnabled) {
        mutableStateOf(currentPreferences.highContrastEnabled)
    }

    SecondaryScreenScaffold(
        title = "Accesibilidad",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 24.dp,
                end = 24.dp,
                bottom = 32.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    Text(
                        text = "Los cambios se aplican y guardan al instante.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    AccessibilitySection("Tamaño de texto") {
                        SettingsRadioGroup(
                            options = TextSizePreference.entries,
                            selected = textSize,
                            label = TextSizePreference::displayName,
                            onSelected = { selected ->
                                textSize = selected
                                onTextSizeChange(selected)
                            },
                        )
                    }

                    AccessibilitySection("Alto contraste") {
                        val themeHighContrast = LocalHighContrastEnabled.current
                        Surface(
                            onClick = {
                                val enabled = !highContrast
                                highContrast = enabled
                                onHighContrastChange(enabled)
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                            shape = MaterialTheme.shapes.large,
                            color = if (themeHighContrast) {
                                MaterialTheme.motivaColors.selectedContainer
                            } else {
                                MaterialTheme.motivaColors.unselectedContainer
                            },
                            contentColor = if (themeHighContrast) {
                                MaterialTheme.motivaColors.selectedContent
                            } else {
                                MaterialTheme.motivaColors.unselectedContent
                            },
                            border = motivaBorderStroke(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = if (highContrast) "Activado" else "Desactivado",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        text = "Refuerza contraste, superficies y bordes.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (themeHighContrast) {
                                            MaterialTheme.motivaColors.selectedContent
                                        } else {
                                            MaterialTheme.motivaColors.secondaryText
                                        },
                                    )
                                }
                                Switch(
                                    checked = highContrast,
                                    onCheckedChange = null,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.motivaColors.selectedContent,
                                        checkedTrackColor = MaterialTheme.motivaColors.selectedContainer,
                                        checkedBorderColor = MaterialTheme.motivaColors.strongBorder,
                                        uncheckedThumbColor = MaterialTheme.motivaColors.unselectedContent,
                                        uncheckedTrackColor = MaterialTheme.motivaColors.unselectedContainer,
                                        uncheckedBorderColor = MaterialTheme.motivaColors.strongBorder,
                                    ),
                                )
                            }
                        }
                    }

                    Text(
                        text = "Más ayudas de lectura podrán añadirse aquí en futuras versiones.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessibilitySection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() },
        )
        content()
    }
}

@Composable
private fun <T> SettingsRadioGroup(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit,
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val highContrast = LocalHighContrastEnabled.current
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelected(option) },
                    ),
                shape = MaterialTheme.shapes.large,
                color = if (highContrast && isSelected) {
                    MaterialTheme.motivaColors.selectedContainer
                } else if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    if (highContrast) {
                        MaterialTheme.motivaColors.unselectedContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    }
                },
                contentColor = if (highContrast && isSelected) {
                    MaterialTheme.motivaColors.selectedContent
                } else if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.motivaColors.unselectedContent
                },
                border = motivaBorderStroke(
                    normalWidth = if (isSelected) 2.dp else 1.dp,
                    normalColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = if (highContrast) {
                                MaterialTheme.motivaColors.selectedContent
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            unselectedColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}
