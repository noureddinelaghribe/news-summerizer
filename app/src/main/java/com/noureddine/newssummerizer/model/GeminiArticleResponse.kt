package com.noureddine.newssummerizer.model

import com.google.gson.annotations.SerializedName

data class GeminiArticleResponse(
    @SerializedName("articles")
    val articles: List<GeminiArticle>
)

data class GeminiArticle(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("link")
    val link: String,
    
    @SerializedName("text")
    val text: String
)

