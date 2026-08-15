package com.example.motivaapp.data.repository

import com.example.motivaapp.data.model.Quote
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteSelectionEngineTest {
    private val engine = QuoteSelectionEngine()

    @Test
    fun `empty optional filters do not restrict candidates`() {
        val quotes = listOf(quote("one", "Directo"), quote("two", "Sereno"))

        val selected = engine.select(
            quotes = quotes,
            request = QuoteSelectionRequest(category = "  ", need = "", sensitivity = null),
            random = Random(3),
        )

        assertTrue(selected in quotes)
    }

    @Test
    fun `editorial eligibility is never relaxed`() {
        val discarded = quote("discarded", "Directo", editorialStatus = "Verificada")
        val lowQuality = quote("low", "Sereno", quality = 3)

        assertNull(engine.select(listOf(discarded, lowQuality)))
    }

    @Test
    fun `strict diversity excludes three ids and two tones`() {
        val quotes = listOf(
            quote("one", "Directo"),
            quote("two", "Sereno"),
            quote("three", "Poético"),
            quote("four", "Cálido"),
            quote("five", "Motivador"),
            quote("six", "Ingenioso"),
        )
        val history = QuoteSelectionHistory(
            recentIds = listOf("one", "two", "three"),
            recentTones = listOf("Cálido", "Motivador"),
        )

        val selected = engine.select(quotes, history = history, random = Random(1))

        assertEquals("six", selected?.id)
    }

    @Test
    fun `small pools degrade diversity instead of returning empty`() {
        val onlyQuote = quote("one", "Directo")
        val history = QuoteSelectionHistory(
            recentIds = listOf("one"),
            recentTones = listOf("Directo"),
        )

        assertEquals(onlyQuote, engine.select(listOf(onlyQuote), history = history))
    }

    @Test
    fun `history stays ordered unique and bounded`() {
        val history = listOf(
            quote("one", "Directo"),
            quote("two", "Sereno"),
            quote("three", "Poético"),
            quote("four", "Directo"),
        ).fold(QuoteSelectionHistory()) { stored, selected -> stored.record(selected) }

        assertEquals(listOf("four", "three", "two"), history.recentIds)
        assertEquals(listOf("Directo", "Poético"), history.recentTones)
    }
}

private fun quote(
    id: String,
    tone: String,
    editorialStatus: String = "Aprobada",
    quality: Int = 5,
) = Quote(
    id = id,
    text = "Texto $id",
    source = "Creación original",
    category = "Motivación",
    need = "Impulso",
    tone = tone,
    rights = "Original",
    verification = "Original",
    quality = quality,
    editorialStatus = editorialStatus,
    sensitivity = "Normal",
)
