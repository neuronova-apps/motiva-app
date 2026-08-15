package com.example.motivaapp.ui.appearance

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.AppearancePreference
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun AppearanceScreen(
    currentAppearance: AppearancePreference,
    onAppearanceChange: (AppearancePreference) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedAppearance by remember(currentAppearance) { mutableStateOf(currentAppearance) }

    SecondaryScreenScaffold(
        title = "Apariencia",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Text(
                        text = "El tema cambia y se guarda al instante.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Column(
                        modifier = Modifier.selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        val highContrast = LocalHighContrastEnabled.current
                        AppearancePreference.entries.forEach { appearance ->
                            val selected = appearance == selectedAppearance
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 64.dp)
                                    .selectable(
                                        selected = selected,
                                        role = Role.RadioButton,
                                        onClick = {
                                            selectedAppearance = appearance
                                            onAppearanceChange(appearance)
                                        },
                                    ),
                                shape = MaterialTheme.shapes.large,
                                color = if (highContrast && selected) {
                                    MaterialTheme.motivaColors.selectedContainer
                                } else if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    if (highContrast) {
                                        MaterialTheme.motivaColors.unselectedContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    }
                                },
                                contentColor = if (highContrast && selected) {
                                    MaterialTheme.motivaColors.selectedContent
                                } else if (selected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.motivaColors.unselectedContent
                                },
                                border = motivaBorderStroke(
                                    normalWidth = if (selected) 2.dp else 1.dp,
                                    normalColor = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                                ),
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 10.dp,
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = selected,
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
                                        text = appearance.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
