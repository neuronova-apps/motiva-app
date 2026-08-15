package com.example.motivaapp.data.preferences

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val IsoDatePattern = "yyyy-MM-dd"

object DeviceLocalDate {
    fun today(): String = formatter().format(Date())

    fun daysEndingAt(date: String, count: Int): List<String> {
        require(count >= 0)
        val calendar = parse(date)
        return List(count) { offset ->
            (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, offset - (count - 1))
            }.let { formatter().format(it.time) }
        }
    }

    internal fun isDayAfter(candidate: String, previous: String): Boolean {
        val expected = parse(previous).apply { add(Calendar.DAY_OF_MONTH, 1) }
        return formatter().format(expected.time) == candidate
    }

    private fun parse(value: String): Calendar {
        val date = formatter().apply { isLenient = false }.parse(value)
            ?: throw IllegalArgumentException("Fecha local no válida: $value")
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 12)
        }
    }

    private fun formatter() = SimpleDateFormat(IsoDatePattern, Locale.ROOT)
}

object DailyUsageTracker {
    fun record(preferences: UserPreferences, date: String): UserPreferences {
        // Validate before changing persistent state.
        DeviceLocalDate.daysEndingAt(date, 1)
        if (date in preferences.usageDates) return preferences

        val previousDate = preferences.lastAccessDate
        val streak = if (
            previousDate != null && DeviceLocalDate.isDayAfter(date, previousDate)
        ) {
            preferences.currentStreak.coerceAtLeast(0) + 1
        } else {
            1
        }
        val dates = preferences.usageDates + date

        return preferences.copy(
            firstUseDate = preferences.firstUseDate ?: date,
            lastAccessDate = date,
            currentStreak = streak,
            bestStreak = maxOf(preferences.bestStreak, streak),
            totalUsageDays = dates.size,
            usageDates = dates,
        )
    }
}
