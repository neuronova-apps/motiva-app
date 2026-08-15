package com.example.motivaapp.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.AstrologyPreference
import com.example.motivaapp.data.preferences.PreferredInterest
import com.example.motivaapp.data.preferences.ZodiacSign
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
internal fun SpiritualityOptions(
    selected: SpiritualityChoice?,
    onSelected: (SpiritualityChoice) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        SpiritualityChoice.SPIRITUAL_AND_RELIGIOUS to
            "Me interesa contenido espiritual y religioso",
        SpiritualityChoice.SPIRITUAL_NOT_RELIGIOUS to
            "Prefiero contenido espiritual, pero no religioso",
        SpiritualityChoice.NEUTRAL to "Prefiero contenido neutral",
        SpiritualityChoice.NO_PREFERENCE to "No tengo preferencia",
    )
    RadioOptions(
        options = options,
        selected = selected,
        onSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
internal fun AstrologyOptions(
    state: OnboardingState,
    onStateChange: (OnboardingState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        RadioOptions(
            options = listOf(
                AstrologyPreference.YES to "Sí",
                AstrologyPreference.NO to "No",
                AstrologyPreference.INDIFFERENT to "Me da igual",
            ),
            selected = state.astrologyPreference,
            onSelected = { onStateChange(state.chooseAstrology(it)) },
        )

        AnimatedVisibility(visible = state.shouldAskForZodiac) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "¿Quieres indicar tu signo?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() },
                )
                RadioOptions(
                    options = ZodiacSign.entries.map { it as ZodiacSign? to it.displayName } +
                        listOf(null to "Prefiero no indicarlo"),
                    selected = state.zodiacSign,
                    isSelected = { option ->
                        state.zodiacChoiceMade && option == state.zodiacSign
                    },
                    onSelected = { onStateChange(state.chooseZodiac(it)) },
                )
            }
        }
    }
}

@Composable
internal fun InterestOptions(
    selected: Set<PreferredInterest>,
    onToggle: (PreferredInterest) -> Unit,
    modifier: Modifier = Modifier,
) {
    val highContrast = LocalHighContrastEnabled.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PreferredInterest.entries.forEach { interest ->
            val isSelected = interest in selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .toggleable(
                        value = isSelected,
                        role = Role.Checkbox,
                        onValueChange = { onToggle(interest) },
                    ),
                shape = MaterialTheme.shapes.large,
                color = if (highContrast && isSelected) {
                    MaterialTheme.motivaColors.selectedContainer
                } else if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
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
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.motivaColors.unselectedContent
                },
                border = motivaBorderStroke(
                    normalWidth = if (isSelected) 2.dp else 1.dp,
                    normalColor = if (isSelected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = if (highContrast) {
                                MaterialTheme.motivaColors.selectedContent
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            checkmarkColor = if (highContrast) {
                                MaterialTheme.motivaColors.selectedContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            },
                            uncheckedColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Text(
                        text = interest.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> RadioOptions(
    options: List<Pair<T, String>>,
    selected: T?,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: ((T) -> Boolean)? = null,
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val highContrast = LocalHighContrastEnabled.current
        options.forEach { (value, label) ->
            val optionSelected = isSelected?.invoke(value) ?: (value == selected)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .selectable(
                        selected = optionSelected,
                        role = Role.RadioButton,
                        onClick = { onSelected(value) },
                    ),
                shape = MaterialTheme.shapes.large,
                color = if (highContrast && optionSelected) {
                    MaterialTheme.motivaColors.selectedContainer
                } else if (optionSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    if (highContrast) {
                        MaterialTheme.motivaColors.unselectedContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    }
                },
                contentColor = if (highContrast && optionSelected) {
                    MaterialTheme.motivaColors.selectedContent
                } else if (optionSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.motivaColors.unselectedContent
                },
                border = motivaBorderStroke(
                    normalWidth = if (optionSelected) 2.dp else 1.dp,
                    normalColor = if (optionSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = optionSelected,
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
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}
