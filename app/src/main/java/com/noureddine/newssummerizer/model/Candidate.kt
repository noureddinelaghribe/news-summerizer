package com.noureddine.newssummerizer.model

data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val index: Int = 0
)