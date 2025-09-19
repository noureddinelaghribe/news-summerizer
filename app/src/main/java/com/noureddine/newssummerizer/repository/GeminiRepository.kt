package com.noureddine.newssummerizer.repository

import com.noureddine.newssummerizer.Interface.GeminiApiService
import com.noureddine.newssummerizer.model.Content
import com.noureddine.newssummerizer.model.GeminiRequest
import com.noureddine.newssummerizer.model.GeminiArticleResponse
import com.noureddine.newssummerizer.model.Part
import com.noureddine.newssummerizer.model.SystemInstruction
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val message: String) : ApiResult<T>()
    data class Loading<T>(val isLoading: Boolean = true) : ApiResult<T>()
}

class GeminiRepository {
    private val apiService: GeminiApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .callTimeout(180, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GeminiApiService::class.java)
    }

    // Attempts to extract a valid JSON object from a Gemini text response.
    // Handles: fenced code blocks, leading/trailing commentary, and quoted JSON strings.
    private fun sanitizeToJson(text: String): String? {
        var candidate = text.trim()

        // Strip markdown code fences ```json ... ``` or ``` ... ```
        if (candidate.startsWith("```")) {
            val end = candidate.lastIndexOf("```")
            if (end > 2) {
                candidate = candidate.substring(3, end).trim()
                // If language tag like 'json' present, drop first line
                val firstNewline = candidate.indexOf('\n')
                if (firstNewline > 0 && candidate.substring(0, firstNewline).matches(Regex("[a-zA-Z0-9_-]+"))) {
                    candidate = candidate.substring(firstNewline + 1).trim()
                }
            }
        }

        // If content includes JSON anywhere, try to isolate the first top-level object
        val startIdx = candidate.indexOf('{')
        val endIdx = candidate.lastIndexOf('}')
        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return candidate.substring(startIdx, endIdx + 1)
        }

        // If wrapped as a JSON string (e.g., "{ ... }"), return as-is to be unquoted later
        if ((candidate.startsWith('"') && candidate.endsWith('"')) ||
            (candidate.startsWith('\'') && candidate.endsWith('\''))) {
            return candidate
        }

        return null
    }

    suspend fun generateContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            systemInstruction = systemInstruction?.let {
                SystemInstruction(
                    parts = listOf(Part(text = it))
                )
            }
        )

        var attempt = 0
        val maxAttempts = 3
        var lastError: String? = null

        while (attempt < maxAttempts) {
            try {
                val response = apiService.generateContent(apiKey, request)
                if (response.isSuccessful) {
                    val geminiResponse = response.body()
                    val generatedText = geminiResponse?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text

                    return@withContext if (generatedText != null) {
                        ApiResult.Success(generatedText)
                    } else {
                        ApiResult.Error("No content generated")
                    }
                } else {
                    return@withContext ApiResult.Error("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: SocketTimeoutException) {
                lastError = "Network Error: timeout"
            } catch (e: IOException) {
                lastError = "Network Error: ${e.localizedMessage}"
            } catch (e: Exception) {
                lastError = e.localizedMessage ?: "Unknown error"
                break
            }

            attempt++
            if (attempt < maxAttempts) {
                delay(1000L * attempt) // simple backoff: 1s, 2s
            }
        }

        ApiResult.Error(lastError ?: "Network Error")
    }

    suspend fun generateStructuredContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null
    ): ApiResult<GeminiArticleResponse> = withContext(Dispatchers.IO) {
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            systemInstruction = systemInstruction?.let {
                SystemInstruction(
                    parts = listOf(Part(text = it))
                )
            }
        )

        var attempt = 0
        val maxAttempts = 3
        var lastError: String? = null

        while (attempt < maxAttempts) {
            try {
                val response = apiService.generateContent(apiKey, request)
                if (response.isSuccessful) {
                    val geminiResponse = response.body()
                    val generatedText = geminiResponse?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text

                    if (generatedText != null) {
                        val gson = Gson()
                        
                        try {
                            // First try: parse directly as JSON object
                            val structuredResponse = gson.fromJson(generatedText, GeminiArticleResponse::class.java)
                            return@withContext ApiResult.Success(structuredResponse)
                        } catch (e: JsonSyntaxException) {
                            // Second try: sanitize and parse
                            val candidateJson = sanitizeToJson(generatedText)
                            if (candidateJson != null) {
                                try {
                                    val structuredResponse = gson.fromJson(candidateJson, GeminiArticleResponse::class.java)
                                    return@withContext ApiResult.Success(structuredResponse)
                                } catch (e2: JsonSyntaxException) {
                                    // Third try: if it's a quoted JSON string, unquote then parse
                                    try {
                                        val unquoted = gson.fromJson(candidateJson, String::class.java)
                                        val structuredResponse = gson.fromJson(unquoted, GeminiArticleResponse::class.java)
                                        return@withContext ApiResult.Success(structuredResponse)
                                    } catch (e3: Exception) {
                                        return@withContext ApiResult.Error("Failed to parse JSON response: ${e3.message}")
                                    }
                                }
                            } else {
                                return@withContext ApiResult.Error("Failed to parse JSON response: No JSON object found")
                            }
                        }
                    } else {
                        return@withContext ApiResult.Error("No content generated")
                    }
                } else {
                    return@withContext ApiResult.Error("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: SocketTimeoutException) {
                lastError = "Network Error: timeout"
            } catch (e: IOException) {
                lastError = "Network Error: ${e.localizedMessage}"
            } catch (e: Exception) {
                lastError = e.localizedMessage ?: "Unknown error"
                break
            }

            attempt++
            if (attempt < maxAttempts) {
                delay(1000L * attempt) // simple backoff: 1s, 2s
            }
        }

        ApiResult.Error(lastError ?: "Network Error")
    }
}