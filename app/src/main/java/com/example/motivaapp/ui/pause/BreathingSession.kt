package com.example.motivaapp.ui.pause

const val PAUSE_DURATION_SECONDS = 30

enum class BreathingPhase(
    val label: String,
    val durationSeconds: Int,
) {
    INHALE("Inhala", 4),
    HOLD("Sostén", 2),
    EXHALE("Exhala", 4),
}

data class BreathingSession(
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
) {
    val remainingSeconds: Int
        get() = (PAUSE_DURATION_SECONDS - elapsedSeconds).coerceAtLeast(0)

    val progress: Float
        get() = elapsedSeconds.toFloat() / PAUSE_DURATION_SECONDS

    val phase: BreathingPhase
        get() {
            val cycleSecond = elapsedSeconds.coerceAtMost(PAUSE_DURATION_SECONDS - 1) %
                BREATHING_CYCLE_SECONDS
            return when {
                cycleSecond < BreathingPhase.INHALE.durationSeconds -> BreathingPhase.INHALE
                cycleSecond < BreathingPhase.INHALE.durationSeconds +
                    BreathingPhase.HOLD.durationSeconds -> BreathingPhase.HOLD
                else -> BreathingPhase.EXHALE
            }
        }

    val phaseRemainingSeconds: Int
        get() {
            if (isFinished) return 0
            val cycleSecond = elapsedSeconds % BREATHING_CYCLE_SECONDS
            val phaseStart = when (phase) {
                BreathingPhase.INHALE -> 0
                BreathingPhase.HOLD -> BreathingPhase.INHALE.durationSeconds
                BreathingPhase.EXHALE -> BreathingPhase.INHALE.durationSeconds +
                    BreathingPhase.HOLD.durationSeconds
            }
            return phase.durationSeconds - (cycleSecond - phaseStart)
        }

    fun start(): BreathingSession = when {
        isFinished -> BreathingSession(isRunning = true)
        else -> copy(isRunning = true)
    }

    fun tick(): BreathingSession {
        if (!isRunning || isFinished) return this
        val nextElapsed = (elapsedSeconds + 1).coerceAtMost(PAUSE_DURATION_SECONDS)
        val finished = nextElapsed == PAUSE_DURATION_SECONDS
        return copy(
            elapsedSeconds = nextElapsed,
            isRunning = !finished,
            isFinished = finished,
        )
    }

    fun reset(): BreathingSession = BreathingSession()

    fun finish(): BreathingSession = BreathingSession(
        elapsedSeconds = PAUSE_DURATION_SECONDS,
        isFinished = true,
    )

    private companion object {
        const val BREATHING_CYCLE_SECONDS = 10
    }
}
