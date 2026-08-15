package com.example.motivaapp.ui.pause

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors
import kotlinx.coroutines.delay

@Composable
fun PauseScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var session by remember { mutableStateOf(BreathingSession()) }

    BackHandler(onBack = onClose)

    LaunchedEffect(session.isRunning) {
        while (session.isRunning) {
            delay(1_000)
            session = session.tick()
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            color = MaterialTheme.motivaColors.appBackground,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Column(
                        modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        PauseHeader(onClose)
                        Text(
                            text = "Relaja el cuerpo. Suelta los hombros. Inhala profundamente " +
                                "por la nariz y exhala lentamente por la boca. Sigue el ritmo " +
                                "hasta terminar.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        BreathingGuide(session)
                        PauseControls(
                            session = session,
                            onStart = { session = session.start() },
                            onReset = { session = session.reset() },
                            onFinish = { session = session.finish() },
                            onClose = onClose,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PauseHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Pausa breve",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "Respiración guiada · 30 segundos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onClose, modifier = Modifier.size(52.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar pausa breve")
        }
    }
}

@Composable
private fun BreathingGuide(session: BreathingSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.cardBackground,
            contentColor = MaterialTheme.motivaColors.primaryText,
        ),
        border = motivaBorderStroke(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = if (session.isFinished) "Bien hecho." else session.phase.label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = when {
                    session.isFinished -> "Tómate un momento antes de continuar."
                    session.isRunning ->
                        "${session.phaseRemainingSeconds} s en esta fase · " +
                            "${session.remainingSeconds} s restantes"
                    else -> "Pulsa Iniciar cuando estés listo."
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Thermometer(session)
                BreathingCircle(session)
            }
            Text(
                text = "Inhala 4 s  •  Sostén 2 s  •  Exhala 4 s",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Thermometer(session: BreathingSession) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics {
            contentDescription =
                "Progreso total: ${session.elapsedSeconds} de $PAUSE_DURATION_SECONDS segundos"
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = session.elapsedSeconds.toFloat(),
                range = 0f..PAUSE_DURATION_SECONDS.toFloat(),
                steps = PAUSE_DURATION_SECONDS - 1,
            )
        },
    ) {
        Text("Progreso", style = MaterialTheme.typography.labelLarge)
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(218.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    border = BorderStroke(3.dp, MaterialTheme.motivaColors.strongBorder),
                    shape = RoundedCornerShape(22.dp),
                )
                .padding(5.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(session.progress.coerceIn(0f, 1f))
                    .background(
                        color = MaterialTheme.motivaColors.primaryAction,
                        shape = RoundedCornerShape(18.dp),
                    ),
            )
        }
        Text(
            text = "${session.elapsedSeconds}/$PAUSE_DURATION_SECONDS s",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun BreathingCircle(session: BreathingSession) {
    val targetSize = when {
        !session.isRunning || session.isFinished -> 112.dp
        session.phase == BreathingPhase.EXHALE -> 112.dp
        else -> 184.dp
    }
    val circleSize by animateDpAsState(
        targetValue = targetSize,
        animationSpec = tween(
            durationMillis = when (session.phase) {
                BreathingPhase.INHALE -> 4_000
                BreathingPhase.HOLD -> 250
                BreathingPhase.EXHALE -> 4_000
            },
        ),
        label = "Guía de respiración",
    )

    Box(
        modifier = Modifier
            .size(196.dp)
            .semantics {
                contentDescription = when {
                    session.isFinished -> "Ejercicio completado"
                    session.isRunning -> "Guía gráfica: ${session.phase.label}"
                    else -> "Guía gráfica preparada para iniciar"
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .background(MaterialTheme.motivaColors.selectedContainer, CircleShape)
                .border(
                    border = BorderStroke(3.dp, MaterialTheme.motivaColors.strongBorder),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when {
                    session.isFinished -> "Listo"
                    session.isRunning -> session.phase.label
                    else -> "Respira"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.motivaColors.selectedContent,
            )
        }
    }
}

@Composable
private fun PauseControls(
    session: BreathingSession,
    onStart: () -> Unit,
    onReset: () -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {
            session.isFinished -> {
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.motivaColors.primaryAction,
                        contentColor = MaterialTheme.motivaColors.primaryActionContent,
                    ),
                ) {
                    Text("Continuar")
                }
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    border = motivaBorderStroke(),
                ) {
                    Text("Reiniciar")
                }
            }
            session.isRunning -> {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    border = motivaBorderStroke(),
                ) {
                    Text("Reiniciar")
                }
                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    border = motivaBorderStroke(),
                ) {
                    Text("Terminar antes")
                }
            }
            else -> Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.motivaColors.primaryAction,
                    contentColor = MaterialTheme.motivaColors.primaryActionContent,
                ),
            ) {
                Text("Iniciar")
            }
        }
    }
}
