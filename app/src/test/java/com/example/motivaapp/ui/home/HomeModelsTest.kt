package com.example.motivaapp.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeModelsTest {

    @Test
    fun `need shortcuts keep their repository query values`() {
        assertEquals("calma", NeedOption.CALM.repositoryNeed)
        assertEquals("seguir adelante", NeedOption.KEEP_GOING.repositoryNeed)
        assertTrue(NeedOption.entries.all { it.repositoryNeed.isNotBlank() })
    }

    @Test
    fun `provisional rhythm always exposes a compact week`() {
        val rhythm = RhythmState()

        assertEquals(7, rhythm.activeDays.size)
        assertEquals(3, rhythm.consecutiveDays)
        assertEquals(3, rhythm.activeDays.count { it })
    }
}
