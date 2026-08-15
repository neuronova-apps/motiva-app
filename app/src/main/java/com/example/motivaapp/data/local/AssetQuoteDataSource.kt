package com.example.motivaapp.data.local

import android.content.res.AssetManager
import com.example.motivaapp.data.model.Quote
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

fun interface QuoteDataSource {
    suspend fun loadQuotes(): List<Quote>
}

/** Lee y deserializa el banco de frases incluido en los assets de la aplicación. */
class AssetQuoteDataSource(
    private val openStream: () -> InputStream,
    private val json: Json = DEFAULT_JSON,
) : QuoteDataSource {

    constructor(
        assets: AssetManager,
        fileName: String = DEFAULT_FILE_NAME,
    ) : this(openStream = { assets.open(fileName) })

    override suspend fun loadQuotes(): List<Quote> = withContext(Dispatchers.IO) {
        openStream().bufferedReader(Charsets.UTF_8).use { reader ->
            json.decodeFromString<List<Quote>>(reader.readText())
        }
    }

    private companion object {
        const val DEFAULT_FILE_NAME = "motiva_quotes.json"

        val DEFAULT_JSON = Json {
            ignoreUnknownKeys = true
            isLenient = false
        }
    }
}
