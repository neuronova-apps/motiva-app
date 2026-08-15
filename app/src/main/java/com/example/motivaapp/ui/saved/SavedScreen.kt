package com.example.motivaapp.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.repository.QuoteRepository
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun SavedScreen(
    repository: QuoteRepository,
    favoriteQuoteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf<SavedState>(SavedState.Loading) }

    LaunchedEffect(repository, favoriteQuoteIds) {
        state = if (favoriteQuoteIds.isEmpty()) {
            SavedState.Loaded(emptyList())
        } else {
            runCatching { repository.getQuotesByIds(favoriteQuoteIds) }.fold(
                onSuccess = SavedState::Loaded,
                onFailure = SavedState::Error,
            )
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.motivaColors.appBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 24.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = "Guardados",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth().semantics { heading() },
                )
            }
            when (val current = state) {
                SavedState.Loading -> item { StatusText("Cargando tus frases…") }
                is SavedState.Error -> item { StatusText("No se pudieron cargar tus frases guardadas.") }
                is SavedState.Loaded -> if (current.quotes.isEmpty()) {
                    item { StatusText("Aún no has guardado frases.") }
                } else {
                    items(current.quotes, key = Quote::id) { quote ->
                        SavedQuoteCard(quote = quote, onRemove = { onToggleFavorite(quote.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth().padding(top = 16.dp),
    )
}

@Composable
private fun SavedQuoteCard(quote: Quote, onRemove: () -> Unit) {
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
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = quote.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(top = 12.dp),
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Quitar de guardados",
                    )
                }
            }
            Text(text = "“${quote.text}”", style = MaterialTheme.typography.titleMedium)
            quote.author?.takeIf(String::isNotBlank)?.let { author ->
                Text(author, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private sealed interface SavedState {
    data object Loading : SavedState
    data class Loaded(val quotes: List<Quote>) : SavedState
    data class Error(val cause: Throwable) : SavedState
}
