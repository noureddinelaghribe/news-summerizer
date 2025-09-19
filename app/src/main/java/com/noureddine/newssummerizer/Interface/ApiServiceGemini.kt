package com.noureddine.newssummerizer.Interface

import com.noureddine.newssummerizer.model.GeminiRequest
import com.noureddine.newssummerizer.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash-lite:generateContent")
    //@POST("v1beta/models/gemini-2.5-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}