package com.example.motivaapp.data.preferences

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementsTest {

    @Test
    fun `first meeting unlocks on first usage day`() {
        val status = statuses(bestStreak = 1, totalDays = 1)
        assertTrue(status.getValue(Achievement.FIRST_MEETING))
    }

    @Test
    fun `streak achievements unlock at 7 15 and 30 days`() {
        val six = statuses(bestStreak = 6, totalDays = 6)
        assertFalse(six.getValue(Achievement.ONE_WEEK))

        val seven = statuses(bestStreak = 7, totalDays = 7)
        assertTrue(seven.getValue(Achievement.ONE_WEEK))
        assertFalse(seven.getValue(Achievement.FIFTEEN_DAYS))

        val fifteen = statuses(bestStreak = 15, totalDays = 15)
        assertTrue(fifteen.getValue(Achievement.FIFTEEN_DAYS))
        assertFalse(fifteen.getValue(Achievement.ONE_MONTH))

        val thirty = statuses(bestStreak = 30, totalDays = 30)
        assertTrue(thirty.getValue(Achievement.ONE_MONTH))
    }

    @Test
    fun `total day achievements unlock independently from current streak`() {
        val fifty = statuses(bestStreak = 2, totalDays = 50)
        assertTrue(fifty.getValue(Achievement.CONSISTENCY))
        assertFalse(fifty.getValue(Achievement.ONE_HUNDRED_DAYS))

        val hundred = statuses(bestStreak = 2, totalDays = 100)
        assertTrue(hundred.getValue(Achievement.ONE_HUNDRED_DAYS))
    }

    @Test
    fun `prepared achievements stay locked until their data exists`() {
        val status = statuses(bestStreak = 365, totalDays = 365)
        assertFalse(status.getValue(Achievement.EXPLORER))
        assertFalse(status.getValue(Achievement.BROAD_VIEW))
        assertFalse(status.getValue(Achievement.MY_FAVORITES))
        assertFalse(status.getValue(Achievement.COLLECTOR))
    }

    @Test
    fun `favorite achievements unlock at ten and fifty real ids`() {
        val nine = statusFor(favoriteCount = 9)
        assertFalse(nine.getValue(Achievement.MY_FAVORITES))

        val ten = statusFor(favoriteCount = 10)
        assertTrue(ten.getValue(Achievement.MY_FAVORITES))
        assertFalse(ten.getValue(Achievement.COLLECTOR))

        val fifty = statusFor(favoriteCount = 50)
        assertTrue(fifty.getValue(Achievement.MY_FAVORITES))
        assertTrue(fifty.getValue(Achievement.COLLECTOR))
    }

    @Test
    fun `explorer unlocks with five distinct categories`() {
        val four = statusFor(explored = (1..4).map { "Categoría $it" }.toSet())
        val five = statusFor(explored = (1..5).map { "Categoría $it" }.toSet())

        assertFalse(four.getValue(Achievement.EXPLORER))
        assertTrue(five.getValue(Achievement.EXPLORER))
    }

    @Test
    fun `broad view uses the categories supplied by the current bank`() {
        val available = setOf("Calma", "Motivación", "Reflexión")
        val incomplete = statusFor(
            explored = setOf("Calma", "Motivación"),
            available = available,
        )
        val complete = statusFor(explored = available, available = available)

        assertFalse(incomplete.getValue(Achievement.BROAD_VIEW))
        assertTrue(complete.getValue(Achievement.BROAD_VIEW))
    }

    private fun statusFor(
        favoriteCount: Int = 0,
        explored: Set<String> = emptySet(),
        available: Set<String> = emptySet(),
    ) = achievementStatuses(
        preferences = UserPreferences.Neutral.copy(
            favoriteQuoteIds = (1..favoriteCount).map { "FR$it" }.toSet(),
            exploredCategories = explored,
        ),
        availableCategories = available,
    ).associate { it.achievement to it.unlocked }

    private fun statuses(bestStreak: Int, totalDays: Int) = achievementStatuses(
        UserPreferences.Neutral.copy(bestStreak = bestStreak, totalUsageDays = totalDays),
    ).associate { it.achievement to it.unlocked }
}
