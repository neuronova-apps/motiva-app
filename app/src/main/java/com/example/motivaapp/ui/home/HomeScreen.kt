package com.example.motivaapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.repository.QuoteRepository
import com.example.motivaapp.data.repository.QuoteSelectionMode
import com.example.motivaapp.data.repository.QuoteSelectionRequest
import com.example.motivaapp.ui.pause.PauseScreen
import com.example.motivaapp.ui.theme.LocalHighContrastEnabled
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    repository: QuoteRepository,
    favoriteQuoteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var quoteState by remember { mutableStateOf<QuoteLoadState>(QuoteLoadState.Loading) }
    var selectedNeed by rememberSaveable { mutableStateOf(NeedOption.CALM) }
    var contextState by remember { mutableStateOf<QuoteLoadState>(QuoteLoadState.Loading) }
    var showPause by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadRandomQuote() {
        scope.launch {
            quoteState = QuoteLoadState.Loading
            quoteState = runCatching { repository.getRandomQuote() }
                .fold(
                    onSuccess = Quote?::toLoadState,
                    onFailure = QuoteLoadState::Error,
                )
        }
    }

    fun loadNeedQuote(need: NeedOption) {
        selectedNeed = need
        scope.launch {
            contextState = QuoteLoadState.Loading
            contextState = runCatching {
                repository.selectQuote(
                    QuoteSelectionRequest(
                        need = need.repositoryNeed,
                        mode = QuoteSelectionMode.CONTEXTUAL,
                    ),
                )
            }.fold(
                onSuccess = Quote?::toLoadState,
                onFailure = QuoteLoadState::Error,
            )
        }
    }

    LaunchedEffect(repository) {
        quoteState = runCatching { repository.getDailyQuote() }
            .fold(
                onSuccess = Quote?::toLoadState,
                onFailure = QuoteLoadState::Error,
            )
        contextState = runCatching {
            repository.selectQuote(
                QuoteSelectionRequest(
                    need = selectedNeed.repositoryNeed,
                    mode = QuoteSelectionMode.CONTEXTUAL,
                ),
            )
        }.fold(
            onSuccess = Quote?::toLoadState,
            onFailure = QuoteLoadState::Error,
        )
    }

    if (showPause) {
        PauseScreen(onClose = { showPause = false })
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.motivaColors.appBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 22.dp,
                end = 20.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            item {
                HomeHeader(onOpenProfile = onOpenProfile)
            }
            item {
                DailyQuoteCard(
                    state = quoteState,
                    isFavorite = (quoteState as? QuoteLoadState.Loaded)?.quote?.id in
                        favoriteQuoteIds,
                    onToggleFavorite = {
                        (quoteState as? QuoteLoadState.Loaded)?.quote?.id?.let(onToggleFavorite)
                    },
                    onAnotherQuote = ::loadRandomQuote,
                    onPause = { showPause = true },
                )
            }
            item {
                SectionTitle("¿Qué necesitas ahora?")
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(NeedOption.entries) { need ->
                        val selected = need == selectedNeed
                        val highContrast = LocalHighContrastEnabled.current
                        val motivaColors = MaterialTheme.motivaColors
                        FilterChip(
                            selected = selected,
                            onClick = { loadNeedQuote(need) },
                            label = {
                                Text(if (selected) "✓ ${need.displayName}" else need.displayName)
                            },
                            colors = if (highContrast) {
                                FilterChipDefaults.filterChipColors(
                                    containerColor = motivaColors.unselectedContainer,
                                    labelColor = motivaColors.unselectedContent,
                                    selectedContainerColor = motivaColors.selectedContainer,
                                    selectedLabelColor = motivaColors.selectedContent,
                                )
                            } else {
                                FilterChipDefaults.filterChipColors()
                            },
                            border = BorderStroke(
                                width = if (highContrast) 3.dp else 1.dp,
                                color = if (highContrast) {
                                    motivaColors.strongBorder
                                } else {
                                    motivaColors.subtleBorder
                                },
                            ),
                        )
                    }
                }
            }
            item {
                ContextQuoteCard(
                    selectedNeed = selectedNeed,
                    state = contextState,
                    onAnother = { loadNeedQuote(selectedNeed) },
                )
            }
            item {
                SectionTitle("Categorías")
                Spacer(Modifier.height(12.dp))
                CategoryRow(
                    onCategorySelected = { category ->
                        scope.launch {
                            runCatching {
                                repository.selectQuote(
                                    QuoteSelectionRequest(
                                        category = category.repositoryCategory,
                                    ),
                                )
                            }.onSuccess { quote ->
                                quote?.let { quoteState = QuoteLoadState.Loaded(it) }
                            }.onFailure { error ->
                                quoteState = QuoteLoadState.Error(error)
                            }
                        }
                    },
                )
            }
            item {
                RhythmCard(state = RhythmState())
            }
        }
    }
}

@Composable
private fun HomeHeader(onOpenProfile: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Motiva",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "Una idea para hoy",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onOpenProfile, modifier = Modifier.size(52.dp)) {
            Icon(Icons.Default.Settings, contentDescription = "Abrir perfil y configuración")
        }
    }
}

@Composable
private fun DailyQuoteCard(
    state: QuoteLoadState,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAnotherQuote: () -> Unit,
    onPause: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.featuredCardBackground,
            contentColor = MaterialTheme.motivaColors.featuredCardContent,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = motivaBorderStroke(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "HOY",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = formattedDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onToggleFavorite, enabled = state is QuoteLoadState.Loaded) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) {
                            "Quitar de guardados"
                        } else {
                            "Guardar frase"
                        },
                    )
                }
            }
            when (state) {
                QuoteLoadState.Loading -> Text("Cargando frase…")
                QuoteLoadState.Empty -> Text("No hay frases disponibles.")
                is QuoteLoadState.Loaded -> {
                    Text(
                        text = "“${state.quote.text}”",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = state.quote.author?.takeIf(String::isNotBlank) ?: "Motiva",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = state.quote.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                is QuoteLoadState.Error -> Text("No se pudo cargar la frase local.")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onAnotherQuote,
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                    border = motivaBorderStroke(),
                ) {
                    Text("Otra frase", textAlign = TextAlign.Center)
                }
                Button(
                    onClick = onPause,
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.motivaColors.primaryAction,
                        contentColor = MaterialTheme.motivaColors.primaryActionContent,
                    ),
                ) {
                    Text("Pausa breve", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun ContextQuoteCard(
    selectedNeed: NeedOption,
    state: QuoteLoadState,
    onAnother: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.motivaColors.contextualCardBackground,
            contentColor = MaterialTheme.motivaColors.contextualCardContent,
        ),
        border = motivaBorderStroke(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = selectedNeed.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = when (state) {
                    QuoteLoadState.Loading -> "Buscando una frase…"
                    QuoteLoadState.Empty ->
                        "Todavía no hay una frase de prueba para esta necesidad."
                    is QuoteLoadState.Loaded -> "“${state.quote.text}”"
                    is QuoteLoadState.Error -> "No se pudo consultar el banco local."
                },
                style = MaterialTheme.typography.bodyLarge,
            )
            OutlinedButton(
                onClick = onAnother,
                enabled = state !is QuoteLoadState.Loading,
                modifier = Modifier.heightIn(min = 48.dp),
                border = motivaBorderStroke(),
            ) {
                Text("Mostrar otra")
            }
        }
    }
}

private data class CategoryShortcut(
    val label: String,
    val repositoryCategory: String,
    val tone: CategoryTone,
)

private enum class CategoryTone { SAGE, BLUE, LAVENDER, SAND }

@Composable
private fun CategoryRow(onCategorySelected: (CategoryShortcut) -> Unit) {
    val categories = remember {
        listOf(
            CategoryShortcut("Calma / Paz y bienestar", "calma", CategoryTone.SAGE),
            CategoryShortcut("Motivación", "motivacion", CategoryTone.BLUE),
            CategoryShortcut("Reflexión", "reflexion", CategoryTone.LAVENDER),
            CategoryShortcut("Humor e ingenio", "humor", CategoryTone.SAND),
        )
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            val highContrast = LocalHighContrastEnabled.current
            val background = if (highContrast) {
                MaterialTheme.motivaColors.cardBackground
            } else when (category.tone) {
                CategoryTone.SAGE -> MaterialTheme.colorScheme.primaryContainer
                CategoryTone.BLUE -> MaterialTheme.colorScheme.secondaryContainer
                CategoryTone.LAVENDER -> MaterialTheme.colorScheme.tertiaryContainer
                CategoryTone.SAND -> MaterialTheme.colorScheme.surfaceVariant
            }
            val foreground = if (highContrast) {
                MaterialTheme.motivaColors.primaryText
            } else when (category.tone) {
                CategoryTone.SAGE -> MaterialTheme.colorScheme.onPrimaryContainer
                CategoryTone.BLUE -> MaterialTheme.colorScheme.onSecondaryContainer
                CategoryTone.LAVENDER -> MaterialTheme.colorScheme.onTertiaryContainer
                CategoryTone.SAND -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Card(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.width(190.dp).heightIn(min = 96.dp),
                colors = CardDefaults.cardColors(containerColor = background),
                shape = MaterialTheme.shapes.extraLarge,
                border = motivaBorderStroke(),
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = foreground,
                    )
                }
            }
        }
    }
}

@Composable
private fun RhythmCard(state: RhythmState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription =
                    "Tu ritmo: ${state.consecutiveDays} días con Motiva en la última semana"
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
            Text("Tu ritmo", style = MaterialTheme.typography.titleMedium)
            Text(
                "${state.consecutiveDays} días con Motiva",
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                state.activeDays.take(7).forEach { active ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
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
                "Vuelve cuando lo necesites.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.semantics { heading() },
    )
}

private fun Quote?.toLoadState(): QuoteLoadState =
    this?.let(QuoteLoadState::Loaded) ?: QuoteLoadState.Empty

private fun formattedDate(): String = SimpleDateFormat(
    "EEEE, d 'de' MMMM",
    Locale.forLanguageTag("es-ES"),
).format(Date()).replaceFirstChar { it.uppercase() }

private sealed interface QuoteLoadState {
    data object Loading : QuoteLoadState
    data object Empty : QuoteLoadState
    data class Loaded(val quote: Quote) : QuoteLoadState
    data class Error(val cause: Throwable) : QuoteLoadState
}
