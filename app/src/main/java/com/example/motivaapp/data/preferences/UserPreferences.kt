package com.example.motivaapp.data.preferences

enum class SpiritualityPreference {
    INTERESTED,
    NEUTRAL,
    NO_PREFERENCE,
}

enum class ReligionPreference {
    INTERESTED,
    NOT_INTERESTED,
    NO_PREFERENCE,
}

enum class AstrologyPreference {
    YES,
    NO,
    INDIFFERENT,
}

enum class ZodiacSign(val displayName: String) {
    ARIES("Aries"),
    TAURUS("Tauro"),
    GEMINI("Géminis"),
    CANCER("Cáncer"),
    LEO("Leo"),
    VIRGO("Virgo"),
    LIBRA("Libra"),
    SCORPIO("Escorpio"),
    SAGITTARIUS("Sagitario"),
    CAPRICORN("Capricornio"),
    AQUARIUS("Acuario"),
    PISCES("Piscis"),
}

enum class PreferredInterest(val displayName: String) {
    CALM("Calma"),
    MOTIVATION("Motivación"),
    REFLECTION("Reflexión"),
    HOPE("Esperanza"),
    RELATIONSHIPS("Relaciones y vínculos"),
    HUMOR("Humor y ligereza"),
    SURPRISE_ME("Sorpréndeme"),
}

enum class TextSizePreference(val displayName: String, val scale: Float) {
    NORMAL("Normal", 1f),
    LARGE("Grande", 1.15f),
    VERY_LARGE("Muy grande", 1.3f),
}

enum class AppearancePreference(val displayName: String) {
    SYSTEM("Seguir sistema"),
    LIGHT("Modo claro"),
    DARK("Modo oscuro"),
}

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val spiritualityPreference: SpiritualityPreference =
        SpiritualityPreference.NO_PREFERENCE,
    val religionPreference: ReligionPreference = ReligionPreference.NO_PREFERENCE,
    val astrologyPreference: AstrologyPreference = AstrologyPreference.INDIFFERENT,
    val zodiacSign: ZodiacSign? = null,
    val preferredInterests: Set<PreferredInterest> = emptySet(),
    val genderPreference: String? = null,
    val textSizePreference: TextSizePreference = TextSizePreference.NORMAL,
    val highContrastEnabled: Boolean = false,
    val appearancePreference: AppearancePreference = AppearancePreference.SYSTEM,
    val displayName: String? = null,
    val firstUseDate: String? = null,
    val lastAccessDate: String? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalUsageDays: Int = 0,
    val usageDates: Set<String> = emptySet(),
    val favoriteQuoteIds: Set<String> = emptySet(),
    val exploredCategories: Set<String> = emptySet(),
) {
    val favoriteCount: Int
        get() = favoriteQuoteIds.size

    val exploredCategoryCount: Int
        get() = exploredCategories.size

    companion object {
        val Neutral = UserPreferences()
    }
}
