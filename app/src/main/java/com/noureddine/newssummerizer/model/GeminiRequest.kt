package com.noureddine.newssummerizer.model

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: SystemInstruction? = null
)