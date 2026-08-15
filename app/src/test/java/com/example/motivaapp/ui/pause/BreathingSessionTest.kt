package com.example.motivaapp.ui.pause

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BreathingSessionTest {

    @Test
    fun `session follows a stable four two four breathing cycle`() {
        var session = BreathingSession().start()

        assertEquals(BreathingPhase.INHALE, session.phase)
        assertEquals(4, session.phaseRemainingSeconds)

        repeat(4) { session = session.tick() }
        assertEquals(BreathingPhase.HOLD, session.phase)
        assertEquals(2, session.phaseRemainingSeconds)

        repeat(2) { session = session.tick() }
        assertEquals(BreathingPhase.EXHALE, session.phase)
        assertEquals(4, session.phaseRemainingSeconds)

        repeat(4) { session = session.tick() }
        assertEquals(BreathingPhase.INHALE, session.phase)
        assertEquals(20, session.remainingSeconds)
    }

    @Test
    fun `progress advances once per tick and never advances before start`() {
        val idle = BreathingSession()
        assertEquals(idle, idle.tick())

        val advanced = idle.start().tick()
        assertEquals(1, advanced.elapsedSeconds)
        assertEquals(29, advanced.remainingSeconds)
        assertEquals(1f / PAUSE_DURATION_SECONDS, advanced.progress, 0.0001f)
    }

    @Test
    fun `session finishes exactly at thirty seconds`() {
        var session = BreathingSession().start()

        repeat(PAUSE_DURATION_SECONDS) { session = session.tick() }

        assertTrue(session.isFinished)
        assertFalse(session.isRunning)
        assertEquals(0, session.remainingSeconds)
        assertEquals(1f, session.progress, 0f)
        assertEquals(session, session.tick())
    }

    @Test
    fun `reset and finish controls produce deterministic states`() {
        val running = BreathingSession().start().tick().tick()

        assertEquals(BreathingSession(), running.reset())
        assertTrue(running.finish().isFinished)
        assertEquals(PAUSE_DURATION_SECONDS, running.finish().elapsedSeconds)
    }
}
