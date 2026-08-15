package com.example.motivaapp.ui.onboarding

import com.example.motivaapp.data.preferences.AstrologyPreference
import com.example.motivaapp.data.preferences.PreferredInterest
import com.example.motivaapp.data.preferences.ReligionPreference
import com.example.motivaapp.data.preferences.SpiritualityPreference
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.ZodiacSign

enum class OnboardingStep {
    WELCOME,
    SPIRITUALITY,
    ASTROLOGY,
    INTERESTS,
    COMPLETE,
}

enum class SpiritualityChoice {
    SPIRITUAL_AND_RELIGIOUS,
    SPIRITUAL_NOT_RELIGIOUS,
    NEUTRAL,
    NO_PREFERENCE,
}

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val spiritualityChoice: SpiritualityChoice? = null,
    val astrologyPreference: AstrologyPreference? = null,
    val zodiacSign: ZodiacSign? = null,
    val zodiacChoiceMade: Boolean = false,
    val preferredInterests: Set<PreferredInterest> = emptySet(),
) {
    val shouldAskForZodiac: Boolean
        get() = astrologyPreference == AstrologyPreference.YES

    val canContinueFromAstrology: Boolean
        get() = astrologyPreference != null &&
            (!shouldAskForZodiac || zodiacChoiceMade)

    fun chooseAstrology(preference: AstrologyPreference): OnboardingState = copy(
        astrologyPreference = preference,
        zodiacSign = if (preference == AstrologyPreference.YES) zodiacSign else null,
        zodiacChoiceMade = if (preference == AstrologyPreference.YES) {
            zodiacChoiceMade
        } else {
            false
        },
    )

    fun chooseZodiac(sign: ZodiacSign?): OnboardingState = copy(
        zodiacSign = sign,
        zodiacChoiceMade = true,
    )

    fun toggleInterest(interest: PreferredInterest): OnboardingState = copy(
        preferredInterests = if (interest in preferredInterests) {
            preferredInterests - interest
        } else {
            preferredInterests + interest
        },
    )

    fun toUserPreferences(onboardingCompleted: Boolean = true): UserPreferences {
        val spirituality = spiritualityChoice ?: SpiritualityChoice.NO_PREFERENCE
        return UserPreferences(
            onboardingCompleted = onboardingCompleted,
            spiritualityPreference = when (spirituality) {
                SpiritualityChoice.SPIRITUAL_AND_RELIGIOUS,
                SpiritualityChoice.SPIRITUAL_NOT_RELIGIOUS,
                -> SpiritualityPreference.INTERESTED
                SpiritualityChoice.NEUTRAL -> SpiritualityPreference.NEUTRAL
                SpiritualityChoice.NO_PREFERENCE -> SpiritualityPreference.NO_PREFERENCE
            },
            religionPreference = when (spirituality) {
                SpiritualityChoice.SPIRITUAL_AND_RELIGIOUS -> ReligionPreference.INTERESTED
                SpiritualityChoice.SPIRITUAL_NOT_RELIGIOUS,
                SpiritualityChoice.NEUTRAL,
                -> ReligionPreference.NOT_INTERESTED
                SpiritualityChoice.NO_PREFERENCE -> ReligionPreference.NO_PREFERENCE
            },
            astrologyPreference = astrologyPreference ?: AstrologyPreference.INDIFFERENT,
            zodiacSign = zodiacSign.takeIf {
                astrologyPreference == AstrologyPreference.YES
            },
            preferredInterests = preferredInterests,
        )
    }

    companion object {
        fun fromPreferences(preferences: UserPreferences): OnboardingState = OnboardingState(
            step = OnboardingStep.SPIRITUALITY,
            spiritualityChoice = when {
                preferences.spiritualityPreference == SpiritualityPreference.INTERESTED &&
                    preferences.religionPreference == ReligionPreference.INTERESTED ->
                    SpiritualityChoice.SPIRITUAL_AND_RELIGIOUS
                preferences.spiritualityPreference == SpiritualityPreference.INTERESTED ->
                    SpiritualityChoice.SPIRITUAL_NOT_RELIGIOUS
                preferences.spiritualityPreference == SpiritualityPreference.NEUTRAL ->
                    SpiritualityChoice.NEUTRAL
                else -> SpiritualityChoice.NO_PREFERENCE
            },
            astrologyPreference = preferences.astrologyPreference,
            zodiacSign = preferences.zodiacSign,
            zodiacChoiceMade = preferences.astrologyPreference == AstrologyPreference.YES,
            preferredInterests = preferences.preferredInterests,
        )
    }
}
