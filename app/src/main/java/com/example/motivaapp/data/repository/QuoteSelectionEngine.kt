package com.example.motivaapp.data.repository

import com.example.motivaapp.data.model.Quote
import java.text.Normalizer
import java.util.Locale
import kotlin.random.Random

enum class QuoteSelectionMode {
    GENERAL,
    CONTEXTUAL,
}

data class QuoteSelectionRequest(
    val category: String? = null,
    val need: String? = null,
    val sensitivity: String? = null,
    val mode: QuoteSelectionMode = QuoteSelectionMode.GENERAL,
)

data class QuoteSelectionHistory(
    val recentIds: List<String> = emptyList(),
    val recentTones: List<String> = emptyList(),
) {
    fun record(quote: Quote): QuoteSelectionHistory = QuoteSelectionHistory(
        recentIds = (listOf(quote.id) + recentIds.filterNot { it == quote.id })
            .filter(String::isNotBlank)
            .take(MAX_RECENT_IDS),
        recentTones = quote.tone?.takeIf(String::isNotBlank)?.let { tone ->
            (listOf(tone) + recentTones.filterNot { it.equals(tone, ignoreCase = true) })
                .take(MAX_RECENT_TONES)
        } ?: recentTones.filter(String::isNotBlank).take(MAX_RECENT_TONES),
    )

    fun bounded(): QuoteSelectionHistory = QuoteSelectionHistory(
        recentIds = recentIds.filter(String::isNotBlank).distinct().take(MAX_RECENT_IDS),
        recentTones = recentTones
            .filter(String::isNotBlank)
            .distinctBy(String::canonicalValue)
            .take(MAX_RECENT_TONES),
    )

    companion object {
        const val MAX_RECENT_IDS = 3
        const val MAX_RECENT_TONES = 2
    }
}

/** Reglas puras de elegibilidad, filtrado y diversidad para todas las selecciones. */
class QuoteSelectionEngine {

    fun productionPool(quotes: List<Quote>): List<Quote> =
        eligibleQuotes(quotes, QuoteSelectionRequest())

    fun eligibleQuotes(
        quotes: List<Quote>,
        request: QuoteSelectionRequest = QuoteSelectionRequest(),
    ): List<Quote> {
        val requestedCategory = request.category.normalizedFilter()
        val requestedNeed = request.need.normalizedFilter()
        val requestedSensitivity = request.sensitivity.normalizedFilter()

        return quotes.filter { quote ->
            quote.hasValidProductionMetadata() &&
                quote.editorialStatus.matchesValue(APPROVED) &&
                (quote.quality ?: 0) >= MINIMUM_QUALITY &&
                quote.hasAllowedSensitivity(request.mode) &&
                (requestedSensitivity == null ||
                    quote.sensitivity.canonicalValue() == requestedSensitivity) &&
                (requestedCategory == null || quote.matchesCategory(requestedCategory)) &&
                (requestedNeed == null || quote.need.canonicalValue() == requestedNeed)
        }
    }

    fun select(
        quotes: List<Quote>,
        request: QuoteSelectionRequest = QuoteSelectionRequest(),
        history: QuoteSelectionHistory = QuoteSelectionHistory(),
        random: Random = Random.Default,
    ): Quote? {
        val candidates = eligibleQuotes(quotes, request)
        if (candidates.isEmpty()) return null

        val boundedHistory = history.bounded()
        val withoutRecentIdsAndTones = candidates.filter { quote ->
            quote.id !in boundedHistory.recentIds &&
                boundedHistory.recentTones.none { it.matchesValue(quote.tone) }
        }
        val withoutRecentIds = candidates.filter { it.id !in boundedHistory.recentIds }
        val withoutImmediateId = candidates.filter { it.id != boundedHistory.recentIds.firstOrNull() }

        return sequenceOf(
            withoutRecentIdsAndTones,
            withoutRecentIds,
            withoutImmediateId,
            candidates,
        ).firstOrNull(List<Quote>::isNotEmpty)?.random(random)
    }

    fun dailyQuote(quotes: List<Quote>, date: String): Quote? {
        val candidates = productionPool(quotes).sortedBy(Quote::id)
        if (candidates.isEmpty()) return null

        val index = (date.hashCode().toLong() and Int.MAX_VALUE.toLong()) % candidates.size
        return candidates[index.toInt()]
    }

    private fun Quote.hasValidProductionMetadata(): Boolean =
        id.isNotBlank() &&
            text.isNotBlank() &&
            source?.isNotBlank() == true &&
            category.isNotBlank() &&
            need.isNotBlank() &&
            tone?.isNotBlank() == true &&
            rights?.isNotBlank() == true &&
            verification?.isNotBlank() == true

    private fun Quote.hasAllowedSensitivity(mode: QuoteSelectionMode): Boolean = when (mode) {
        QuoteSelectionMode.GENERAL -> sensitivity.matchesValue(NORMAL)
        QuoteSelectionMode.CONTEXTUAL -> CONTEXTUAL_SENSITIVITIES.any {
            sensitivity.matchesValue(it)
        }
    }

    private fun Quote.matchesCategory(requestedCategory: String): Boolean =
        category.canonicalValue() == requestedCategory ||
            secondaryCategories.any { it.canonicalValue() == requestedCategory }

    private companion object {
        const val APPROVED = "Aprobada"
        const val NORMAL = "Normal"
        const val MINIMUM_QUALITY = 4
        val CONTEXTUAL_SENSITIVITIES = setOf(NORMAL, "Emocional", "Contextual")
    }
}

private fun String?.normalizedFilter(): String? =
    this?.trim()?.takeIf(String::isNotEmpty)?.canonicalValue()

private fun String?.matchesValue(other: String?): Boolean =
    canonicalValue() == other.canonicalValue()

private fun String?.canonicalValue(): String = Normalizer
    .normalize(this.orEmpty().trim(), Normalizer.Form.NFD)
    .replace(COMBINING_MARKS, "")
    .lowercase(Locale.ROOT)

private val COMBINING_MARKS = "\\p{M}+".toRegex()
