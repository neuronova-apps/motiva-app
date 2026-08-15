package com.example.motivaapp.data.repository

import com.example.motivaapp.data.local.AssetQuoteDataSource
import com.example.motivaapp.data.model.Quote
import com.example.motivaapp.data.preferences.QuoteSelectionHistoryStore
import java.io.File
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteRepositoryTest {

    private val assetFile: File
        get() = sequenceOf(
            File("src/main/assets/motiva_quotes.json"),
            File("app/src/main/assets/motiva_quotes.json"),
        ).first { it.isFile }

    @Test
    fun `repository reads the complete definitive asset preserving ids`() = runBlocking {
        val quotes = repositoryFromAsset().loadAllQuotes()

        assertEquals(1_193, quotes.size)
        assertEquals("FR000001", quotes.first().id)
        assertEquals(1_193, quotes.map(Quote::id).distinct().size)
        assertFalse(quotes.any { it.id == "FR000544" })
        assertTrue(quotes.all { it.text.isNotBlank() })
    }

    @Test
    fun `general production pool has the validated count and metadata`() = runBlocking {
        val pool = repositoryFromAsset().getProductionPool()

        assertEquals(1_178, pool.size)
        assertTrue(pool.all { it.editorialStatus == "Aprobada" })
        assertTrue(pool.all { it.sensitivity == "Normal" })
        assertTrue(pool.all { (it.quality ?: 0) >= 4 })
        assertFalse(pool.any { it.editorialStatus == "Verificada" })
    }

    @Test
    fun `repository exposes the nineteen real primary categories once`() = runBlocking {
        val categories = repositoryFromAsset().getCategories()

        assertEquals(19, categories.size)
        assertEquals(categories.distinct(), categories)
        assertTrue("Calma" in categories)
        assertTrue("Motivación" in categories)
        assertTrue("Sabiduría y aprendizaje" in categories)
    }

    @Test
    fun `missing saved ids are ignored while existing ids resolve`() = runBlocking {
        val repository = repositoryFromAsset()
        val requested = setOf("FR000001", "ID_REMOVED_IN_FUTURE")

        val quotes = repository.getQuotesByIds(requested)
        val existingIds = repository.getExistingQuoteIds(requested)

        assertEquals(listOf("FR000001"), quotes.map(Quote::id))
        assertEquals(setOf("FR000001"), existingIds)
    }

    @Test
    fun `category and need filters are accent insensitive and production safe`() = runBlocking {
        val repository = repositoryFromAsset()

        val motivation = repository.getQuotesByCategory(" MOTIVACION ")
        val calm = repository.getQuotesByNeed("CALMA")
        val withoutCategory = repository.getQuotesByCategory("   ")
        val withoutNeed = repository.getQuotesByNeed("")

        assertTrue(motivation.isNotEmpty())
        assertTrue(motivation.all { quote ->
            quote.category == "Motivación" || "Motivación" in quote.secondaryCategories
        })
        assertTrue(calm.isNotEmpty())
        assertTrue(calm.all { it.need == "Calma" })
        assertTrue((motivation + calm).all { it.sensitivity == "Normal" })
        assertEquals(1_178, withoutCategory.size)
        assertEquals(1_178, withoutNeed.size)
    }

    @Test
    fun `general and contextual modes keep emotional quotes separated`() = runBlocking {
        val repository = repositoryFromAsset()

        val general = repository.selectQuote(
            request = QuoteSelectionRequest(sensitivity = "Emocional"),
            random = Random(1),
        )
        val contextual = repository.selectQuote(
            request = QuoteSelectionRequest(
                need = "Calma",
                sensitivity = "Emocional",
                mode = QuoteSelectionMode.CONTEXTUAL,
            ),
            random = Random(1),
        )

        assertEquals(null, general)
        assertNotNull(contextual)
        assertEquals("Emocional", contextual?.sensitivity)
        assertEquals("Calma", contextual?.need)
    }

    @Test
    fun `consecutive selections avoid the last three ids and last two tones`() = runBlocking {
        val historyStore = InMemoryHistoryStore()
        val repository = QuoteRepository(
            dataSource = AssetQuoteDataSource(openStream = assetFile::inputStream),
            historyStore = historyStore,
        )

        repeat(30) { iteration ->
            val before = historyStore.load()
            val selected = repository.getRandomQuote(Random(iteration))

            assertNotNull(selected)
            assertFalse(selected?.id in before.recentIds)
            assertFalse(before.recentTones.any { it.equals(selected?.tone, ignoreCase = true) })
        }
    }

    @Test
    fun `daily quote is stable and always belongs to production`() = runBlocking {
        val repository = repositoryFromAsset()

        val first = repository.getDailyQuote("2026-08-10")
        val second = repository.getDailyQuote("2026-08-10")

        assertEquals(first?.id, second?.id)
        assertNotNull(first)
        assertTrue(first in repository.getProductionPool())
    }

    @Test
    fun `category selection goes through engine eligibility and history`() = runBlocking {
        val historyStore = InMemoryHistoryStore()
        val repository = QuoteRepository(
            dataSource = AssetQuoteDataSource(openStream = assetFile::inputStream),
            historyStore = historyStore,
        )

        val first = repository.selectQuote(
            request = QuoteSelectionRequest(category = "Motivación"),
            random = Random(11),
        )
        val second = repository.selectQuote(
            request = QuoteSelectionRequest(category = "Motivación"),
            random = Random(12),
        )

        assertNotNull(first)
        assertNotNull(second)
        assertTrue(first?.category == "Motivación" || "Motivación" in first.orEmptySecondary())
        assertTrue(second?.category == "Motivación" || "Motivación" in second.orEmptySecondary())
        assertFalse(first?.id == second?.id)
        assertTrue(first?.id in historyStore.load().recentIds)
        assertTrue(second?.id in historyStore.load().recentIds)
    }

    private fun repositoryFromAsset() = QuoteRepository(
        AssetQuoteDataSource(openStream = assetFile::inputStream),
    )
}

private fun Quote?.orEmptySecondary(): List<String> = this?.secondaryCategories.orEmpty()

private class InMemoryHistoryStore : QuoteSelectionHistoryStore {
    private var history = QuoteSelectionHistory()

    override suspend fun load(): QuoteSelectionHistory = history

    override suspend fun recordDisplayed(quote: Quote) {
        history = history.record(quote)
    }
}
