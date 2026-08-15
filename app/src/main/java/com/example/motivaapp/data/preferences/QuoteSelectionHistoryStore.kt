package com.example.motivaapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.repository.QuoteSelectionHistory
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface QuoteSelectionHistoryStore {
    suspend fun load(): QuoteSelectionHistory

    suspend fun recordDisplayed(quote: Quote)
}

object EmptyQuoteSelectionHistoryStore : QuoteSelectionHistoryStore {
    override suspend fun load() = QuoteSelectionHistory()

    override suspend fun recordDisplayed(quote: Quote) = Unit
}

/** Historial técnico separado de preferencias, favoritos y cualquier historial visible. */
class DataStoreQuoteSelectionHistoryStore(
    private val dataStore: DataStore<Preferences>,
    private val json: Json = Json,
) : QuoteSelectionHistoryStore {

    override suspend fun load(): QuoteSelectionHistory = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .first()
        .toHistory()
        .bounded()

    override suspend fun recordDisplayed(quote: Quote) {
        dataStore.edit { stored ->
            val updated = stored.toHistory().record(quote)
            stored[RECENT_IDS] = json.encodeToString(updated.recentIds)
            stored[RECENT_TONES] = json.encodeToString(updated.recentTones)
        }
    }

    private fun Preferences.toHistory() = QuoteSelectionHistory(
        recentIds = decodeList(this[RECENT_IDS]),
        recentTones = decodeList(this[RECENT_TONES]),
    )

    private fun decodeList(value: String?): List<String> = value?.let { encoded ->
        runCatching { json.decodeFromString<List<String>>(encoded) }.getOrDefault(emptyList())
    }.orEmpty()

    private companion object {
        val RECENT_IDS = stringPreferencesKey("recent_quote_ids")
        val RECENT_TONES = stringPreferencesKey("recent_quote_tones")
    }
}

val Context.quoteSelectionHistoryDataStore by preferencesDataStore(
    name = "motiva_quote_selection_history",
)
