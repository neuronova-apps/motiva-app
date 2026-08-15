package com.example.motivaapp.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.preferences.UserPreferences

@Composable
fun OnboardingScreen(
    onComplete: (UserPreferences) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(OnboardingState()) }
    val currentStepNumber = state.step.ordinal + 1

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
            ) {
                Text(
                    text = "Paso $currentStepNumber de 5",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LinearProgressIndicator(
                    progress = { currentStepNumber / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 28.dp),
                )

                when (state.step) {
                    OnboardingStep.WELCOME -> WelcomeStep(
                        onStart = { state = state.copy(step = OnboardingStep.SPIRITUALITY) },
                        onSkip = onSkip,
                    )
                    OnboardingStep.SPIRITUALITY -> QuestionStep(
                        title = "¿Qué tipo de contenido espiritual prefieres?",
                        onBack = { state = state.copy(step = OnboardingStep.WELCOME) },
                        onContinue = { state = state.copy(step = OnboardingStep.ASTROLOGY) },
                        continueEnabled = state.spiritualityChoice != null,
                    ) {
                        SpiritualityOptions(
                            selected = state.spiritualityChoice,
                            onSelected = { state = state.copy(spiritualityChoice = it) },
                        )
                    }
                    OnboardingStep.ASTROLOGY -> QuestionStep(
                        title = "¿Te interesa la astrología y el zodiaco?",
                        onBack = { state = state.copy(step = OnboardingStep.SPIRITUALITY) },
                        onContinue = { state = state.copy(step = OnboardingStep.INTERESTS) },
                        continueEnabled = state.canContinueFromAstrology,
                    ) {
                        AstrologyOptions(
                            state = state,
                            onStateChange = { state = it },
                        )
                    }
                    OnboardingStep.INTERESTS -> QuestionStep(
                        title = "¿Qué te gustaría encontrar más en Motiva?",
                        supportingText = "Puedes elegir varias opciones o continuar sin seleccionar ninguna.",
                        onBack = { state = state.copy(step = OnboardingStep.ASTROLOGY) },
                        onContinue = { state = state.copy(step = OnboardingStep.COMPLETE) },
                        continueEnabled = true,
                    ) {
                        InterestOptions(
                            selected = state.preferredInterests,
                            onToggle = { state = state.toggleInterest(it) },
                        )
                    }
                    OnboardingStep.COMPLETE -> FinalStep(
                        onBack = { state = state.copy(step = OnboardingStep.INTERESTS) },
                        onComplete = { onComplete(state.toUserPreferences()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onStart: () -> Unit, onSkip: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        StepTitle("Motiva, un poco más cerca de ti")
        Text(
            text = "Puedes contarnos algunas preferencias para ayudarte a encontrar frases más afines a lo que buscas. Todo podrá cambiarse después.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text("Comenzar")
        }
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text("Configurar más tarde")
        }
    }
}

@Composable
private fun QuestionStep(
    title: String,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    continueEnabled: Boolean,
    supportingText: String? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        StepTitle(title)
        supportingText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        content()
        NavigationButtons(
            onBack = onBack,
            onContinue = onContinue,
            continueEnabled = continueEnabled,
        )
    }
}

@Composable
private fun FinalStep(onBack: () -> Unit, onComplete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        StepTitle("Todo listo")
        Text(
            text = "Motiva utilizará estas preferencias para organizar mejor las frases que ves. Puedes cambiarlas cuando quieras desde Perfil > Preferencias.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text("Entrar a Motiva")
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Composable
private fun NavigationButtons(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    continueEnabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.weight(1f).height(52.dp),
        ) {
            Text("Atrás")
        }
        Button(
            onClick = onContinue,
            enabled = continueEnabled,
            modifier = Modifier.weight(1f).height(52.dp),
        ) {
            Text("Continuar")
        }
    }
}

@Composable
private fun StepTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.semantics { heading() },
    )
}
