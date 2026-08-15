package com.example.motivaapp.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.motivaapp.data.model.Quote
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class QuoteSelectionHistoryStoreTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `selection history remains bounded after datastore restart`() = runBlocking {
        val file = File(temporaryFolder.root, "quote-history.preferences_pb")
        val firstScope = testScope()
        val firstStore = store(file, firstScope)
        firstStore.recordDisplayed(quote("one", "Directo"))
        firstStore.recordDisplayed(quote("two", "Sereno"))
        firstStore.recordDisplayed(quote("three", "Poético"))
        firstStore.recordDisplayed(quote("four", "Directo"))

        assertEquals(listOf("four", "three", "two"), firstStore.load().recentIds)
        assertEquals(listOf("Directo", "Poético"), firstStore.load().recentTones)
        closeScope(firstScope)

        val secondScope = testScope()
        try {
            val restored = store(file, secondScope).load()
            assertEquals(listOf("four", "three", "two"), restored.recentIds)
            assertEquals(listOf("Directo", "Poético"), restored.recentTones)
        } finally {
            closeScope(secondScope)
        }
    }

    private fun store(file: File, scope: CoroutineScope) =
        DataStoreQuoteSelectionHistoryStore(
            PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }),
        )

    private fun testScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun closeScope(scope: CoroutineScope) {
        val job = scope.coroutineContext[Job]
        scope.cancel()
        if (job != null) joinAll(job)
    }

    private fun quote(id: String, tone: String) = Quote(
        id = id,
        text = "Texto $id",
        source = "Creación original",
        category = "Motivación",
        need = "Impulso",
        tone = tone,
        rights = "Original",
        verification = "Original",
        quality = 5,
        editorialStatus = "Aprobada",
        sensitivity = "Normal",
    )
}
