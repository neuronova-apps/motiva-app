package com.example.motivaapp.ui.explore

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.repository.QuoteRepository
import com.example.motivaapp.data.repository.QuoteSelectionRequest
import com.example.motivaapp.ui.components.SecondaryScreenScaffold
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    category: String,
    repository: QuoteRepository,
    favoriteQuoteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var quoteState by remember(category) {
        mutableStateOf<CategoryQuoteState>(CategoryQuoteState.Loading)
    }
    val scope = rememberCoroutineScope()

    fun loadQuote() {
        scope.launch {
            quoteState = CategoryQuoteState.Loading
            quoteState = runCatching {
                repository.selectQuote(QuoteSelectionRequest(category = category))
            }.fold(
                onSuccess = { quote -> quote?.let(CategoryQuoteState::Loaded) ?: CategoryQuoteState.Empty },
                onFailure = CategoryQuoteState::Error,
            )
        }
    }

    LaunchedEffect(repository, category) { loadQuote() }

    SecondaryScreenScaffold(
        title = category,
        onBack = onBack,
        backContentDescription = "Volver a Explorar",
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(20.dp, 24.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Card(
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.motivaColors.featuredCardBackground,
                        contentColor = MaterialTheme.motivaColors.featuredCardContent,
                    ),
                    border = motivaBorderStroke(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        when (val state = quoteState) {
                            CategoryQuoteState.Loading -> Text("Buscando una frase…")
                            CategoryQuoteState.Empty -> Text("No hay frases disponibles en esta categoría.")
                            is CategoryQuoteState.Error -> Text("No se pudo consultar el banco local.")
                            is CategoryQuoteState.Loaded -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f).padding(top = 12.dp),
                                    )
                                    val isFavorite = state.quote.id in favoriteQuoteIds
                                    IconButton(onClick = { onToggleFavorite(state.quote.id) }) {
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
                                Text(
                                    text = "“${state.quote.text}”",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                state.quote.author?.takeIf(String::isNotBlank)?.let { author ->
                                    Text(author, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = ::loadQuote,
                    enabled = quoteState !is CategoryQuoteState.Loading,
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth().heightIn(min = 52.dp),
                    border = motivaBorderStroke(),
                ) {
                    Text("Otra frase")
                }
            }
        }
    }
}

private sealed interface CategoryQuoteState {
    data object Loading : CategoryQuoteState
    data object Empty : CategoryQuoteState
    data class Loaded(val quote: Quote) : CategoryQuoteState
    data class Error(val cause: Throwable) : CategoryQuoteState
}
