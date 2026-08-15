package com.example.motivaapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.userPreferencesDataStore by preferencesDataStore(
    name = "motiva_user_preferences",
)
