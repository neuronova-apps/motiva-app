package com.example.motivaapp.data.model

import kotlinx.serialization.Serializable

/**
 * Frase y metadatos editoriales usados por el banco local de Motiva.
 *
 * Los valores opcionales y sus defaults permiten incorporar el banco definitivo
 * de forma gradual sin romper la lectura de registros todavía incompletos.
 */
@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String? = null,
    val source: String? = null,
    val category: String,
    val secondaryCategories: List<String> = emptyList(),
    val need: String,
    val tone: String? = null,
    val spirituality: String? = null,
    val religion: String? = null,
    val astrology: String? = null,
    val zodiacSign: String? = null,
    val rights: String? = null,
    val verification: String? = null,
    val quality: Int? = null,
    val editorialStatus: String? = null,
    val sensitivity: String? = null,
)
