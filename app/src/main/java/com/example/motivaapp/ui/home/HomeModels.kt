package com.example.motivaapp.ui.home

enum class NeedOption(val displayName: String, val repositoryNeed: String) {
    CALM("Calma", "calma"),
    HOPE("Esperanza", "esperanza"),
    KEEP_GOING("Seguir adelante", "seguir adelante"),
    FOCUS("Enfocarme", "enfocarme"),
    UNDERSTAND_MYSELF("Comprenderme", "comprenderme"),
    LIGHTEN("Aligerar", "aligerar"),
}

data class RhythmState(
    val consecutiveDays: Int = 3,
    val activeDays: List<Boolean> = listOf(true, true, true, false, false, false, false),
)
