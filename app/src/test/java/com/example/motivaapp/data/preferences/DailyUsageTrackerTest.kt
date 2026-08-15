package com.example.motivaapp.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyUsageTrackerTest {

    @Test
    fun `first opening creates first day current streak and best streak`() {
        val result = DailyUsageTracker.record(UserPreferences.Neutral, "2026-08-01")

        assertEquals("2026-08-01", result.firstUseDate)
        assertEquals("2026-08-01", result.lastAccessDate)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
        assertEquals(1, result.totalUsageDays)
    }

    @Test
    fun `opening many times on the same date counts one usage day`() {
        var result = UserPreferences.Neutral
        repeat(10) { result = DailyUsageTracker.record(result, "2026-08-01") }

        assertEquals(1, result.totalUsageDays)
        assertEquals(1, result.currentStreak)
        assertEquals(setOf("2026-08-01"), result.usageDates)
    }

    @Test
    fun `next local day increments current and best streak`() {
        val dayOne = DailyUsageTracker.record(UserPreferences.Neutral, "2026-08-01")
        val dayTwo = DailyUsageTracker.record(dayOne, "2026-08-02")

        assertEquals(2, dayTwo.currentStreak)
        assertEquals(2, dayTwo.bestStreak)
        assertEquals(2, dayTwo.totalUsageDays)
    }

    @Test
    fun `gap starts a new streak without reducing best streak`() {
        val afterFiveDays = recordConsecutiveDays(5)
        val result = DailyUsageTracker.record(afterFiveDays, "2026-08-10")

        assertEquals(1, result.currentStreak)
        assertEquals(5, result.bestStreak)
        assertEquals(6, result.totalUsageDays)
    }

    @Test
    fun `different non consecutive dates update total distinct days`() {
        val dates = listOf("2026-01-01", "2026-01-10", "2026-02-15", "2026-04-20")
        val result = dates.fold(UserPreferences.Neutral, DailyUsageTracker::record)

        assertEquals(4, result.totalUsageDays)
        assertEquals(dates.toSet(), result.usageDates)
    }

    private fun recordConsecutiveDays(count: Int): UserPreferences =
        DeviceLocalDate.daysEndingAt("2026-08-05", count)
            .fold(UserPreferences.Neutral, DailyUsageTracker::record)
}
