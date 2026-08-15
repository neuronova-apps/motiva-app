package com.example.motivaapp.data.preferences

enum class Achievement(
    val title: String,
    val description: String,
    val available: Boolean = true,
) {
    FIRST_MEETING("PRIMER ENCUENTRO", "Tu primer día con Motiva."),
    ONE_WEEK("UNA SEMANA CONTIGO", "Alcanza 7 días consecutivos."),
    FIFTEEN_DAYS("QUINCE DÍAS", "Alcanza 15 días consecutivos."),
    ONE_MONTH("UN MES CON MOTIVA", "Alcanza 30 días consecutivos."),
    CONSISTENCY("CONSTANCIA", "Abre Motiva en 50 días distintos."),
    ONE_HUNDRED_DAYS("100 DÍAS", "Abre Motiva en 100 días distintos."),
    EXPLORER("EXPLORADOR", "Visita 5 categorías."),
    BROAD_VIEW("MIRADA AMPLIA", "Visita todas las categorías."),
    MY_FAVORITES("MIS FAVORITAS", "Guarda 10 frases."),
    COLLECTOR("COLECCIONISTA", "Guarda 50 frases."),
}

data class AchievementStatus(
    val achievement: Achievement,
    val unlocked: Boolean,
)

fun achievementStatuses(
    preferences: UserPreferences,
    availableCategories: Set<String> = emptySet(),
): List<AchievementStatus> =
    Achievement.entries.map { achievement ->
        AchievementStatus(
            achievement = achievement,
            unlocked = achievement.available && when (achievement) {
                Achievement.FIRST_MEETING -> preferences.totalUsageDays >= 1
                Achievement.ONE_WEEK -> preferences.bestStreak >= 7
                Achievement.FIFTEEN_DAYS -> preferences.bestStreak >= 15
                Achievement.ONE_MONTH -> preferences.bestStreak >= 30
                Achievement.CONSISTENCY -> preferences.totalUsageDays >= 50
                Achievement.ONE_HUNDRED_DAYS -> preferences.totalUsageDays >= 100
                Achievement.EXPLORER -> preferences.exploredCategoryCount >= 5
                Achievement.BROAD_VIEW -> availableCategories.isNotEmpty() &&
                    preferences.exploredCategories.containsAll(availableCategories)
                Achievement.MY_FAVORITES -> preferences.favoriteCount >= 10
                Achievement.COLLECTOR -> preferences.favoriteCount >= 50
            },
        )
    }
