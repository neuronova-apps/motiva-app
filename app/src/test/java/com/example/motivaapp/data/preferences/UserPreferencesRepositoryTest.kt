package com.example.motivaapp.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UserPreferencesRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `configure later completes onboarding with neutral defaults`() = withRepository { repository ->
        repository.completeWithNeutralDefaults()

        val preferences = repository.preferences.first()

        assertTrue(preferences.onboardingCompleted)
        assertEquals(SpiritualityPreference.NO_PREFERENCE, preferences.spiritualityPreference)
        assertEquals(ReligionPreference.NO_PREFERENCE, preferences.religionPreference)
        assertEquals(AstrologyPreference.INDIFFERENT, preferences.astrologyPreference)
        assertNull(preferences.zodiacSign)
        assertTrue(preferences.preferredInterests.isEmpty())
    }

    @Test
    fun `preferences are saved and remain after datastore restarts`() = runBlocking {
        val file = File(temporaryFolder.root, "restart.preferences_pb")
        val expected = UserPreferences(
            onboardingCompleted = true,
            spiritualityPreference = SpiritualityPreference.INTERESTED,
            religionPreference = ReligionPreference.NOT_INTERESTED,
            astrologyPreference = AstrologyPreference.YES,
            zodiacSign = ZodiacSign.LIBRA,
            preferredInterests = setOf(
                PreferredInterest.CALM,
                PreferredInterest.RELATIONSHIPS,
            ),
            textSizePreference = TextSizePreference.VERY_LARGE,
            highContrastEnabled = true,
            appearancePreference = AppearancePreference.DARK,
        )

        val firstScope = testScope()
        val firstRepository = repository(file, firstScope)
        firstRepository.save(expected)
        assertEquals(expected, firstRepository.preferences.first())
        closeScope(firstScope)

        val secondScope = testScope()
        try {
            val reopenedRepository = repository(file, secondScope)
            assertEquals(expected, reopenedRepository.preferences.first())
        } finally {
            closeScope(secondScope)
        }
    }

    @Test
    fun `saved preferences can be modified later`() = withRepository { repository ->
        repository.save(
            UserPreferences(
                onboardingCompleted = true,
                astrologyPreference = AstrologyPreference.YES,
                zodiacSign = ZodiacSign.ARIES,
                preferredInterests = setOf(PreferredInterest.MOTIVATION),
            ),
        )
        repository.save(
            UserPreferences(
                onboardingCompleted = true,
                spiritualityPreference = SpiritualityPreference.NEUTRAL,
                religionPreference = ReligionPreference.NOT_INTERESTED,
                astrologyPreference = AstrologyPreference.NO,
                preferredInterests = setOf(
                    PreferredInterest.HOPE,
                    PreferredInterest.HUMOR,
                ),
                textSizePreference = TextSizePreference.LARGE,
                highContrastEnabled = true,
                appearancePreference = AppearancePreference.LIGHT,
            ),
        )

        val updated = repository.preferences.first()

        assertTrue(updated.onboardingCompleted)
        assertEquals(SpiritualityPreference.NEUTRAL, updated.spiritualityPreference)
        assertEquals(AstrologyPreference.NO, updated.astrologyPreference)
        assertNull(updated.zodiacSign)
        assertEquals(
            setOf(PreferredInterest.HOPE, PreferredInterest.HUMOR),
            updated.preferredInterests,
        )
        assertEquals(TextSizePreference.LARGE, updated.textSizePreference)
        assertTrue(updated.highContrastEnabled)
        assertEquals(AppearancePreference.LIGHT, updated.appearancePreference)
    }

    @Test
    fun `display name is saved and can be modified`() = withRepository { repository ->
        repository.save(UserPreferences.Neutral.copy(displayName = "Luna"))
        assertEquals("Luna", repository.preferences.first().displayName)

        repository.update { it.copy(displayName = "Sol") }
        assertEquals("Sol", repository.preferences.first().displayName)
    }

    @Test
    fun `display name remains after datastore restarts`() = runBlocking {
        val file = File(temporaryFolder.root, "display-name.preferences_pb")
        val firstScope = testScope()
        val firstRepository = repository(file, firstScope)
        firstRepository.save(UserPreferences.Neutral.copy(displayName = "Alex"))
        closeScope(firstScope)

        val secondScope = testScope()
        try {
            assertEquals("Alex", repository(file, secondScope).preferences.first().displayName)
        } finally {
            closeScope(secondScope)
        }
    }

    @Test
    fun `blank display name is persisted as omitted`() = withRepository { repository ->
        repository.save(UserPreferences.Neutral.copy(displayName = "   "))

        assertNull(repository.preferences.first().displayName)
    }

    @Test
    fun `atomic updates preserve accessibility choices made in quick succession`() =
        withRepository { repository ->
            repository.save(UserPreferences.Neutral.copy(onboardingCompleted = true))
            repository.update { it.copy(textSizePreference = TextSizePreference.VERY_LARGE) }
            repository.update { it.copy(highContrastEnabled = true) }
            repository.update { it.copy(appearancePreference = AppearancePreference.DARK) }

            val updated = repository.preferences.first()

            assertEquals(TextSizePreference.VERY_LARGE, updated.textSizePreference)
            assertTrue(updated.highContrastEnabled)
            assertEquals(AppearancePreference.DARK, updated.appearancePreference)
        }

    @Test
    fun `favorite can be saved queried and removed`() = withRepository { repository ->
        repository.toggleFavorite("FR000001")

        assertTrue("FR000001" in repository.preferences.first().favoriteQuoteIds)
        assertEquals(1, repository.preferences.first().favoriteCount)

        repository.toggleFavorite("FR000001")

        assertFalse("FR000001" in repository.preferences.first().favoriteQuoteIds)
        assertEquals(0, repository.preferences.first().favoriteCount)
    }

    @Test
    fun `multiple favorites remain after datastore restart`() = runBlocking {
        val file = File(temporaryFolder.root, "favorites-restart.preferences_pb")
        val firstScope = testScope()
        val firstRepository = repository(file, firstScope)
        firstRepository.toggleFavorite("FR000001")
        firstRepository.toggleFavorite("FR000002")
        firstRepository.toggleFavorite("FR000003")
        closeScope(firstScope)

        val secondScope = testScope()
        try {
            val reopened = repository(file, secondScope).preferences.first()
            assertEquals(setOf("FR000001", "FR000002", "FR000003"), reopened.favoriteQuoteIds)
            assertEquals(3, reopened.favoriteCount)
        } finally {
            closeScope(secondScope)
        }
    }

    @Test
    fun `orphan favorite ids can be cleaned atomically`() = withRepository { repository ->
        repository.save(
            UserPreferences.Neutral.copy(
                favoriteQuoteIds = setOf("FR000001", "REMOVED_ID"),
            ),
        )

        repository.retainExistingFavorites(setOf("FR000001"))

        assertEquals(setOf("FR000001"), repository.preferences.first().favoriteQuoteIds)
    }

    @Test
    fun `explored categories are unique and persistent`() = runBlocking {
        val file = File(temporaryFolder.root, "categories-restart.preferences_pb")
        val firstScope = testScope()
        val firstRepository = repository(file, firstScope)
        firstRepository.recordExploredCategory("Calma")
        firstRepository.recordExploredCategory("Calma")
        firstRepository.recordExploredCategory("Motivación")
        assertEquals(2, firstRepository.preferences.first().exploredCategoryCount)
        closeScope(firstScope)

        val secondScope = testScope()
        try {
            assertEquals(
                setOf("Calma", "Motivación"),
                repository(file, secondScope).preferences.first().exploredCategories,
            )
        } finally {
            closeScope(secondScope)
        }
    }

    @Test
    fun `default datastore value has not completed onboarding`() = withRepository { repository ->
        assertFalse(repository.preferences.first().onboardingCompleted)
    }

    private fun withRepository(
        block: suspend (DataStoreUserPreferencesRepository) -> Unit,
    ) = runBlocking {
        val scope = testScope()
        try {
            block(repository(File(temporaryFolder.root, "test.preferences_pb"), scope))
        } finally {
            closeScope(scope)
        }
    }

    private fun repository(
        file: File,
        scope: CoroutineScope,
    ) = DataStoreUserPreferencesRepository(
        PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { file },
        ),
    )

    private fun testScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun closeScope(scope: CoroutineScope) {
        val job = scope.coroutineContext[Job]
        scope.cancel()
        if (job != null) joinAll(job)
    }
}
