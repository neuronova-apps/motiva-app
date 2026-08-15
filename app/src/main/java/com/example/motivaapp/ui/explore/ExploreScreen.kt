package com.example.motivaapp.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaapp.ui.theme.motivaBorderStroke
import com.example.motivaapp.ui.theme.motivaColors

@Composable
fun ExploreScreen(
    categories: List<String>,
    onOpenCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.motivaColors.appBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 24.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = "Explorar",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth().semantics { heading() },
                )
            }
            item {
                Text(
                    text = "Encuentra una frase para el tema que quieras acompañar hoy.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                )
            }
            if (categories.isEmpty()) {
                item {
                    Text(
                        text = "Cargando categorías…",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth().padding(top = 16.dp),
                    )
                }
            } else {
                items(categories, key = { it }) { category ->
                    Card(
                        onClick = { onOpenCategory(category) },
                        modifier = Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxWidth()
                            .heightIn(min = 76.dp)
                            .semantics { role = Role.Button },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.motivaColors.cardBackground,
                            contentColor = MaterialTheme.motivaColors.primaryText,
                        ),
                        border = motivaBorderStroke(),
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                        )
                    }
                }
            }
        }
    }
}
