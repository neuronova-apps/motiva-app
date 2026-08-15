package com.example.motivaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.motivaapp.data.local.AssetQuoteDataSource
import com.example.motivaapp.data.preferences.DataStoreUserPreferencesRepository
import com.example.motivaapp.data.preferences.DataStoreQuoteSelectionHistoryStore
import com.example.motivaapp.data.preferences.quoteSelectionHistoryDataStore
import com.example.motivaapp.data.preferences.userPreferencesDataStore
import com.example.motivaapp.data.repository.QuoteRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val quoteRepository = remember {
                QuoteRepository(
                    dataSource = AssetQuoteDataSource(applicationContext.assets),
                    historyStore = DataStoreQuoteSelectionHistoryStore(
                        applicationContext.quoteSelectionHistoryDataStore,
                    ),
                )
            }
            val preferencesRepository = remember {
                DataStoreUserPreferencesRepository(applicationContext.userPreferencesDataStore)
            }

            MotivaApp(
                quoteRepository = quoteRepository,
                preferencesRepository = preferencesRepository,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
