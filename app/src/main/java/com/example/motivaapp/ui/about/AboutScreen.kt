package com.example.motivaapp.ui.about

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.motivaapp.BuildConfig
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors
import kotlinx.coroutines.launch

private const val MotivaUrl = "https://neuronova-apps.github.io/motiva-apps/"
private const val NeuronovaUrl = "https://neuronova-apps.github.io/"
private const val PrivacyUrl = "https://neuronova-apps.github.io/devocional-app/privacy/"

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun openExternalLink(label: String, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
            .onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No hay una aplicación disponible para abrir $label.",
                    )
                }
            }
    }

    SecondaryScreenScaffold(
        title = "Acerca de",
        onBack = onBack,
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 24.dp,
                end = 20.dp,
                bottom = 32.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                AboutCard {
                    CardHeading("Aplicación: Motiva")
                    Text(
                        text = "Versión: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Ofrecer frases breves de reflexión, motivación, inspiración y acompañamiento cotidiano mediante una experiencia accesible, serena y personalizada.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Motiva utiliza un banco de frases cuidadosamente organizado por categorías, necesidades, tonos y preferencias de contenido.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "La aplicación tiene un propósito general de reflexión, inspiración y entretenimiento, y no sustituye atención psicológica, terapéutica ni profesional.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                AboutCard {
                    CardHeading("Neuronova Apps")
                    Text(
                        text = "Motiva forma parte de Neuronova Apps.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Proyecto personal desarrollado por Gabriel Berrospi.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    HorizontalDivider()
                    ExternalLinkRow(
                        label = "Visitar sitio de Motiva",
                        onOpen = { openExternalLink("el sitio de Motiva", MotivaUrl) },
                    )
                    HorizontalDivider()
                    ExternalLinkRow(
                        label = "Conocer Neuronova Apps",
                        onOpen = { openExternalLink("Neuronova Apps", NeuronovaUrl) },
                    )
                    HorizontalDivider()
                    ExternalLinkRow(
                        label = "Política de privacidad",
                        onOpen = {
                            openExternalLink("la Política de privacidad", PrivacyUrl)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.cardBackground,
            contentColor = MaterialTheme.motivaColors.primaryText,
        ),
        border = motivaBorderStroke(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun CardHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.semantics { heading() },
    )
}

@Composable
private fun ExternalLinkRow(
    label: String,
    supportingText: String? = null,
    onOpen: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Button(
            onClick = { onOpen?.invoke() },
            enabled = onOpen != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.motivaColors.primaryAction,
                contentColor = MaterialTheme.motivaColors.primaryActionContent,
            ),
            modifier = Modifier
                .heightIn(min = 48.dp)
                .semantics {
                    contentDescription = if (onOpen == null) {
                        "$label: enlace no disponible"
                    } else {
                        "Abrir $label en el navegador"
                    }
                },
        ) {
            Text("Abrir")
        }
    }
}
