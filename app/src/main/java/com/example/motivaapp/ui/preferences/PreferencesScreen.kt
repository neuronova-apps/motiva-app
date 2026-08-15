package com.example.motivaapp.ui.preferences

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors
import com.example.motivaapp.ui.onboarding.AstrologyOptions
import com.example.motivaapp.ui.onboarding.InterestOptions
import com.example.motivaapp.ui.onboarding.OnboardingState
import com.example.motivaapp.ui.onboarding.SpiritualityOptions

@Composable
fun PreferencesScreen(
    currentPreferences: UserPreferences,
    onSave: (UserPreferences) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember(currentPreferences) {
        mutableStateOf(OnboardingState.fromPreferences(currentPreferences))
    }

    SecondaryScreenScaffold(
        title = "Preferencias",
        onBack = onCancel,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                    PreferenceSection("Contenido espiritual y religioso") {
                        SpiritualityOptions(
                            selected = state.spiritualityChoice,
                            onSelected = { state = state.copy(spiritualityChoice = it) },
                        )
                    }

                    PreferenceSection("Astrología y zodiaco") {
                        AstrologyOptions(state = state, onStateChange = { state = it })
                    }

                    PreferenceSection(
                        title = "Intereses",
                        supportingText = "Estas elecciones orientarán la selección; no excluirán otros contenidos.",
                    ) {
                        InterestOptions(
                            selected = state.preferredInterests,
                            onToggle = { state = state.toggleInterest(it) },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                            border = motivaBorderStroke(),
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val selected = state.toUserPreferences()
                                onSave(
                                    currentPreferences.copy(
                                        onboardingCompleted = true,
                                        spiritualityPreference = selected.spiritualityPreference,
                                        religionPreference = selected.religionPreference,
                                        astrologyPreference = selected.astrologyPreference,
                                        zodiacSign = selected.zodiacSign,
                                        preferredInterests = selected.preferredInterests,
                                    ),
                                )
                            },
                            enabled = state.spiritualityChoice != null &&
                                state.canContinueFromAstrology,
                            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.motivaColors.primaryAction,
                                contentColor = MaterialTheme.motivaColors.primaryActionContent,
                            ),
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceSection(
    title: String,
    supportingText: String? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() },
        )
        supportingText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        content()
    }
}
