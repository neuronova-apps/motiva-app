package com.example.motivaapp.ui.onboarding

import com.example.motivaapp.AppDestination
import com.example.motivaapp.data.preferences.AstrologyPreference
import com.example.motivaapp.data.preferences.PreferredInterest
import com.example.motivaapp.data.preferences.ReligionPreference
import com.example.motivaapp.data.preferences.SpiritualityPreference
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.ZodiacSign
import com.example.motivaapp.destinationFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingStateTest {

    @Test
    fun `onboarding is the first destination until it is completed`() {
        assertEquals(AppDestination.ONBOARDING, destinationFor(UserPreferences.Neutral))
        assertEquals(
            AppDestination.HOME,
            destinationFor(UserPreferences.Neutral.copy(onboardingCompleted = true)),
        )
    }

    @Test
    fun `zodiac is requested only when astrology is yes`() {
        val initial = OnboardingState()
        val yes = initial.chooseAstrology(AstrologyPreference.YES)
            .chooseZodiac(ZodiacSign.PISCES)
        val no = yes.chooseAstrology(AstrologyPreference.NO)

        assertTrue(yes.shouldAskForZodiac)
        assertFalse(no.shouldAskForZodiac)
        assertNull(no.zodiacSign)
    }

    @Test
    fun `more than one interest can be selected and deselected`() {
        val selected = OnboardingState()
            .toggleInterest(PreferredInterest.CALM)
            .toggleInterest(PreferredInterest.HOPE)

        assertEquals(
            setOf(PreferredInterest.CALM, PreferredInterest.HOPE),
            selected.preferredInterests,
        )
        assertEquals(
            setOf(PreferredInterest.HOPE),
            selected.toggleInterest(PreferredInterest.CALM).preferredInterests,
        )
    }

    @Test
    fun `onboarding choices map to quote bank preference fields`() {
        val preferences = OnboardingState(
            spiritualityChoice = SpiritualityChoice.SPIRITUAL_NOT_RELIGIOUS,
            astrologyPreference = AstrologyPreference.YES,
            zodiacSign = ZodiacSign.VIRGO,
            zodiacChoiceMade = true,
            preferredInterests = setOf(PreferredInterest.REFLECTION),
        ).toUserPreferences()

        assertTrue(preferences.onboardingCompleted)
        assertEquals(SpiritualityPreference.INTERESTED, preferences.spiritualityPreference)
        assertEquals(ReligionPreference.NOT_INTERESTED, preferences.religionPreference)
        assertEquals(AstrologyPreference.YES, preferences.astrologyPreference)
        assertEquals(ZodiacSign.VIRGO, preferences.zodiacSign)
        assertEquals(setOf(PreferredInterest.REFLECTION), preferences.preferredInterests)
    }
}
