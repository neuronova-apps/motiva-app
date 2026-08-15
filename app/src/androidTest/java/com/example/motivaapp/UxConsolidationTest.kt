package com.example.motivaapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.motivaapp.data.local.QuoteDataSource
import com.example.motivaapp.data.preferences.UserPreferences
import com.example.motivaapp.data.preferences.UserPreferencesRepository
import com.example.motivaapp.data.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class UxConsolidationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profile_separates_personal_usage_from_configuration() {
        setMotivaContent()

        composeRule.onNodeWithText("Perfil").performClick()

        composeRule.onNodeWithText("Tu constancia").assertIsDisplayed()
        composeRule.onNodeWithText("Mis logros").assertIsDisplayed()
        composeRule.onNodeWithText("Editar nombre").assertIsDisplayed()
        composeRule.onNodeWithText("Configuración").performClick()

        composeRule.onNodeWithText("Preferencias").assertIsDisplayed()
        composeRule.onNodeWithText("Accesibilidad").assertIsDisplayed()
        composeRule.onNodeWithText("Apariencia").assertIsDisplayed()
        composeRule.onNodeWithText("Acerca de").assertIsDisplayed()
    }

    @Test
    fun system_back_and_top_arrow_return_secondary_screens_to_configuration() {
        setMotivaContent()
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.onNodeWithText("Configuración").performClick()

        composeRule.onNodeWithText("Preferencias").performClick()
        pressSystemBack()
        assertReturnedToConfiguration()

        composeRule.onNodeWithText("Accesibilidad").performClick()
        composeRule.onNodeWithContentDescription("Volver a Configuración").performClick()
        assertReturnedToConfiguration()

        composeRule.onNodeWithText("Apariencia").performClick()
        pressSystemBack()
        assertReturnedToConfiguration()

        composeRule.onNodeWithText("Acerca de").performClick()
        composeRule.onNodeWithContentDescription("Volver a Configuración").performClick()
        assertReturnedToConfiguration()

        pressSystemBack()
        composeRule.onNodeWithText("Tu constancia").assertIsDisplayed()
    }

    @Test
    fun achievements_return_directly_to_profile() {
        setMotivaContent()
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.onNodeWithText("Mis logros").performClick()

        composeRule.onNodeWithText("PRIMER ENCUENTRO").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver a Perfil").performClick()
        composeRule.onNodeWithText("Tu constancia").assertIsDisplayed()
    }

    @Test
    fun display_name_can_be_added_from_profile() {
        setMotivaContent()
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.onNodeWithText("Editar nombre").performClick()

        composeRule.onNode(hasSetTextAction()).performTextInput("Luna")
        composeRule.onNodeWithText("Guardar").performClick()

        composeRule.onNodeWithText("Hola, Luna").assertIsDisplayed()
    }

    @Test
    fun about_screen_contains_version_disclaimer_and_external_actions() {
        setMotivaContent()
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.onNodeWithText("Configuración").performClick()
        composeRule.onNodeWithText("Acerca de").performClick()

        composeRule.onNodeWithText("Aplicación: Motiva").assertIsDisplayed()
        composeRule.onNodeWithText("Versión: ${BuildConfig.VERSION_NAME}").assertIsDisplayed()
        composeRule.onNodeWithText("Visitar sitio de Motiva").assertIsDisplayed()
        composeRule.onNodeWithText("Conocer Neuronova Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Política de privacidad")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun pause_opens_as_a_guided_exercise_and_returns_to_today() {
        setMotivaContent()

        composeRule.onNodeWithText("Pausa breve").performClick()

        composeRule.onNodeWithText("Respiración guiada · 30 segundos").assertIsDisplayed()
        composeRule.onNodeWithText("Inhala").assertIsDisplayed()
        composeRule.onNodeWithText("Inhala 4 s  •  Sostén 2 s  •  Exhala 4 s")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Iniciar").performClick()
        composeRule.onNodeWithText("Terminar antes").performClick()
        composeRule.onNodeWithText("Bien hecho.").assertIsDisplayed()
        composeRule.onNodeWithText("Continuar").performClick()

        composeRule.onNodeWithText("Una idea para hoy").assertIsDisplayed()
    }

    @Test
    fun system_back_closes_pause_without_leaving_today() {
        setMotivaContent()
        composeRule.onNodeWithText("Pausa breve").performClick()
        composeRule.onNodeWithText("Respiración guiada · 30 segundos").assertIsDisplayed()

        pressSystemBack()

        composeRule.onNodeWithText("Una idea para hoy").assertIsDisplayed()
        composeRule.onNodeWithText("Pausa breve").assertIsDisplayed()
    }

    private fun setMotivaContent() {
        composeRule.setContent {
            MotivaApp(
                quoteRepository = QuoteRepository(QuoteDataSource { emptyList() }),
                preferencesRepository = FakePreferencesRepository(
                    UserPreferences.Neutral.copy(onboardingCompleted = true),
                ),
            )
        }
        composeRule.waitForIdle()
    }

    private fun assertReturnedToConfiguration() {
        composeRule.onNodeWithText(
            "Ajusta Motiva para que se sienta más cómoda para ti.",
        ).assertIsDisplayed()
    }

    private fun pressSystemBack() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val activity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
                .filterIsInstance<ComponentActivity>()
                .single()
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
    }
}

private class FakePreferencesRepository(initial: UserPreferences) : UserPreferencesRepository {
    private val state = MutableStateFlow(initial)

    override val preferences: Flow<UserPreferences> = state

    override suspend fun save(preferences: UserPreferences) {
        state.value = preferences
    }

    override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        state.value = transform(state.value)
    }
}
