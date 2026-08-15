package com.example.motivaapp.data.repository

import com.example.motivaapp.data.local.QuoteDataSource
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.preferences.DeviceLocalDate
import com.example.motivaapp.data.preferences.EmptyQuoteSelectionHistoryStore
import com.example.motivaapp.data.preferences.QuoteSelectionHistoryStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

class QuoteRepository(
    private val dataSource: QuoteDataSource,
    private val historyStore: QuoteSelectionHistoryStore = EmptyQuoteSelectionHistoryStore,
    private val selectionEngine: QuoteSelectionEngine = QuoteSelectionEngine(),
) {
    @Volatile
    private var cachedQuotes: List<Quote>? = null
    private val selectionMutex = Mutex()

    suspend fun loadAllQuotes(forceRefresh: Boolean = false): List<Quote> {
        if (!forceRefresh) cachedQuotes?.let { return it }

        return dataSource.loadQuotes().also { loadedQuotes ->
            cachedQuotes = loadedQuotes
        }
    }

    suspend fun getProductionPool(): List<Quote> =
        selectionEngine.productionPool(loadAllQuotes())

    suspend fun getCategories(): List<String> = getProductionPool()
        .map(Quote::category)
        .filter(String::isNotBlank)
        .distinct()
        .sorted()

    suspend fun getQuotesByIds(ids: Set<String>): List<Quote> {
        if (ids.isEmpty()) return emptyList()
        return loadAllQuotes().filter { it.id in ids }
    }

    suspend fun getExistingQuoteIds(ids: Set<String>): Set<String> {
        if (ids.isEmpty()) return emptySet()
        return loadAllQuotes().asSequence()
            .map(Quote::id)
            .filter(ids::contains)
            .toSet()
    }

    suspend fun getQuotesByCategory(category: String): List<Quote> {
        return selectionEngine.eligibleQuotes(
            quotes = loadAllQuotes(),
            request = QuoteSelectionRequest(category = category),
        )
    }

    suspend fun getQuotesByNeed(need: String): List<Quote> {
        return selectionEngine.eligibleQuotes(
            quotes = loadAllQuotes(),
            request = QuoteSelectionRequest(need = need),
        )
    }

    suspend fun getRandomQuote(random: Random = Random.Default): Quote? =
        selectQuote(random = random)

    suspend fun selectQuote(
        request: QuoteSelectionRequest = QuoteSelectionRequest(),
        random: Random = Random.Default,
    ): Quote? = selectionMutex.withLock {
        val quote = selectionEngine.select(
            quotes = loadAllQuotes(),
            request = request,
            history = historyStore.load(),
            random = random,
        )
        quote?.also { historyStore.recordDisplayed(it) }
    }

    suspend fun getDailyQuote(date: String = DeviceLocalDate.today()): Quote? =
        selectionMutex.withLock {
            selectionEngine.dailyQuote(loadAllQuotes(), date)
                ?.also { historyStore.recordDisplayed(it) }
        }
}
