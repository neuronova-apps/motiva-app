package com.example.motivaapp.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun save(preferences: UserPreferences)

    suspend fun update(transform: (UserPreferences) -> UserPreferences)

    suspend fun recordDailyUsage(date: String = DeviceLocalDate.today()) {
        update { stored -> DailyUsageTracker.record(stored, date) }
    }

    suspend fun toggleFavorite(quoteId: String) {
        val normalizedId = quoteId.trim()
        if (normalizedId.isEmpty()) return
        update { stored ->
            stored.copy(
                favoriteQuoteIds = if (normalizedId in stored.favoriteQuoteIds) {
                    stored.favoriteQuoteIds - normalizedId
                } else {
                    stored.favoriteQuoteIds + normalizedId
                },
            )
        }
    }

    suspend fun retainExistingFavorites(existingQuoteIds: Set<String>) {
        update { stored ->
            stored.copy(favoriteQuoteIds = stored.favoriteQuoteIds intersect existingQuoteIds)
        }
    }

    suspend fun recordExploredCategory(category: String) {
        val normalizedCategory = category.trim()
        if (normalizedCategory.isEmpty()) return
        update { stored ->
            stored.copy(exploredCategories = stored.exploredCategories + normalizedCategory)
        }
    }

    suspend fun retainExistingExploredCategories(existingCategories: Set<String>) {
        update { stored ->
            stored.copy(
                exploredCategories = stored.exploredCategories intersect existingCategories,
            )
        }
    }

    suspend fun completeWithNeutralDefaults() {
        update { stored ->
            stored.copy(
                onboardingCompleted = true,
                spiritualityPreference = SpiritualityPreference.NO_PREFERENCE,
                religionPreference = ReligionPreference.NO_PREFERENCE,
                astrologyPreference = AstrologyPreference.INDIFFERENT,
                zodiacSign = null,
                preferredInterests = emptySet(),
            )
        }
    }
}

class DataStoreUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map(::mapPreferences)

    override suspend fun save(preferences: UserPreferences) {
        dataStore.edit { stored ->
            stored.write(preferences)
        }
    }

    override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        dataStore.edit { stored ->
            stored.write(transform(mapPreferences(stored)))
        }
    }

    private fun MutablePreferences.write(preferences: UserPreferences) {
        this[Keys.ONBOARDING_COMPLETED] = preferences.onboardingCompleted
        this[Keys.SPIRITUALITY] = preferences.spiritualityPreference.name
        this[Keys.RELIGION] = preferences.religionPreference.name
        this[Keys.ASTROLOGY] = preferences.astrologyPreference.name
        this[Keys.TEXT_SIZE] = preferences.textSizePreference.name
        this[Keys.HIGH_CONTRAST] = preferences.highContrastEnabled
        this[Keys.APPEARANCE] = preferences.appearancePreference.name
        this[Keys.INTERESTS] = preferences.preferredInterests.mapTo(mutableSetOf()) { it.name }
        this[Keys.CURRENT_STREAK] = preferences.currentStreak
        this[Keys.BEST_STREAK] = preferences.bestStreak
        this[Keys.TOTAL_USAGE_DAYS] = preferences.totalUsageDays
        this[Keys.USAGE_DATES] = preferences.usageDates
        this[Keys.FAVORITE_QUOTE_IDS] = preferences.favoriteQuoteIds
        this[Keys.EXPLORED_CATEGORIES] = preferences.exploredCategories

        preferences.zodiacSign?.let { this[Keys.ZODIAC_SIGN] = it.name }
            ?: remove(Keys.ZODIAC_SIGN)
        preferences.genderPreference?.let { this[Keys.GENDER] = it }
            ?: remove(Keys.GENDER)
        preferences.displayName?.trim()?.takeIf(String::isNotEmpty)
            ?.let { this[Keys.DISPLAY_NAME] = it }
            ?: remove(Keys.DISPLAY_NAME)
        preferences.firstUseDate?.let { this[Keys.FIRST_USE_DATE] = it }
            ?: remove(Keys.FIRST_USE_DATE)
        preferences.lastAccessDate?.let { this[Keys.LAST_ACCESS_DATE] = it }
            ?: remove(Keys.LAST_ACCESS_DATE)
    }

    private fun mapPreferences(stored: Preferences): UserPreferences = UserPreferences(
        onboardingCompleted = stored[Keys.ONBOARDING_COMPLETED] ?: false,
        spiritualityPreference = stored[Keys.SPIRITUALITY]
            .toEnumOrDefault(SpiritualityPreference.NO_PREFERENCE),
        religionPreference = stored[Keys.RELIGION]
            .toEnumOrDefault(ReligionPreference.NO_PREFERENCE),
        astrologyPreference = stored[Keys.ASTROLOGY]
            .toEnumOrDefault(AstrologyPreference.INDIFFERENT),
        zodiacSign = stored[Keys.ZODIAC_SIGN].toEnumOrNull<ZodiacSign>(),
        preferredInterests = stored[Keys.INTERESTS]
            .orEmpty()
            .mapNotNullTo(mutableSetOf()) { value ->
                value.toEnumOrNull<PreferredInterest>()
            },
        genderPreference = stored[Keys.GENDER],
        textSizePreference = stored[Keys.TEXT_SIZE]
            .toEnumOrDefault(TextSizePreference.NORMAL),
        highContrastEnabled = stored[Keys.HIGH_CONTRAST] ?: false,
        appearancePreference = stored[Keys.APPEARANCE]
            .toEnumOrDefault(AppearancePreference.SYSTEM),
        displayName = stored[Keys.DISPLAY_NAME]?.trim()?.takeIf(String::isNotEmpty),
        firstUseDate = stored[Keys.FIRST_USE_DATE],
        lastAccessDate = stored[Keys.LAST_ACCESS_DATE],
        currentStreak = stored[Keys.CURRENT_STREAK] ?: 0,
        bestStreak = stored[Keys.BEST_STREAK] ?: 0,
        totalUsageDays = stored[Keys.TOTAL_USAGE_DAYS] ?: 0,
        usageDates = stored[Keys.USAGE_DATES].orEmpty(),
        favoriteQuoteIds = stored[Keys.FAVORITE_QUOTE_IDS].orEmpty(),
        exploredCategories = stored[Keys.EXPLORED_CATEGORIES].orEmpty(),
    ).let { preferences ->
        if (preferences.astrologyPreference == AstrologyPreference.YES) preferences
        else preferences.copy(zodiacSign = null)
    }

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SPIRITUALITY = stringPreferencesKey("spirituality_preference")
        val RELIGION = stringPreferencesKey("religion_preference")
        val ASTROLOGY = stringPreferencesKey("astrology_preference")
        val ZODIAC_SIGN = stringPreferencesKey("zodiac_sign")
        val INTERESTS = stringSetPreferencesKey("preferred_interests")
        val GENDER = stringPreferencesKey("gender_preference")
        val TEXT_SIZE = stringPreferencesKey("text_size_preference")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast_enabled")
        val APPEARANCE = stringPreferencesKey("appearance_preference")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val FIRST_USE_DATE = stringPreferencesKey("first_use_date")
        val LAST_ACCESS_DATE = stringPreferencesKey("last_access_date")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val TOTAL_USAGE_DAYS = intPreferencesKey("total_usage_days")
        val USAGE_DATES = stringSetPreferencesKey("usage_dates")
        val FAVORITE_QUOTE_IDS = stringSetPreferencesKey("favorite_quote_ids")
        val EXPLORED_CATEGORIES = stringSetPreferencesKey("explored_categories")
    }
}

private inline fun <reified T : Enum<T>> String?.toEnumOrNull(): T? =
    this?.let { value -> enumValues<T>().firstOrNull { it.name == value } }

private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T =
    toEnumOrNull<T>() ?: default
