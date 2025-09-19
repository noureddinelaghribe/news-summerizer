package com.noureddine.newssummerizer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noureddine.newssummerizer.Utel.AESEncryptor
import com.noureddine.newssummerizer.model.GeminiArticleResponse
import com.noureddine.newssummerizer.repository.ApiResult
import com.noureddine.newssummerizer.repository.GeminiRepository
import kotlinx.coroutines.launch

class GeminiViewModel(
    private val repository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _response = MutableLiveData<String?>()
    val response: LiveData<String?> = _response

    private val _structuredResponse = MutableLiveData<GeminiArticleResponse?>()
    val structuredResponse: LiveData<GeminiArticleResponse?> = _structuredResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Replace with your actual API key
    //private val apiKey = AESEncryptor.quickDecrypt("2US1cn/Bw4lu5oCsbQKlBy8gS/Yehhtd8jaehWM3bYMbWbjno/jSkoW/Z8dsQXGceKaOGL6vJFhbhnAJYQM9vA==")
    //private val apiKey = AESEncryptor.quickDecrypt("s87DuFa79l5OWiuURhusipxskGuf/J5701MplEdULKnvN+6wDqPYiTqYiSrqW0y2Kjnl6NTPYkvbVdn8VR6new==")
    //private val apiKey = AESEncryptor.quickDecrypt("y0LWr+A83r4+eckXNINrWm6ohlRrhsP6dG7fSfYK9KzeJhhGsBls4QuhKgK0FquD46gzB4WvD/nBu7Yt1lNk0Q==")
    private val apiKey = AESEncryptor.quickDecrypt("araDKv03zvq7C1FYvEYSwDYqjbeFEbFoUazNl8geagoG1z3Ky4vadBXhOfn+iI/eJ2/La8sA+q+2WokV/Sr1PQ==")



    fun generateContent(prompt: String, systemInstruction: String? = null) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _loading.postValue(true)
            try {
                when (val result = repository.generateStructuredContent(apiKey, prompt, systemInstruction)) {
                    is ApiResult.Success -> {
                        // For backward compatibility, also set the raw response
                        _response.postValue("Structured response received")
                        _structuredResponse.postValue(result.data)
                        _error.postValue(null)
                    }
                    is ApiResult.Error -> {
                        _error.postValue(result.message)
                    }
                    is ApiResult.Loading -> {
                        // no-op, loading handled by _loading flag
                    }
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun generateStructuredContent(prompt: String, systemInstruction: String? = null) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _loading.postValue(true)
            try {
                when (val result = repository.generateStructuredContent(apiKey, prompt, systemInstruction)) {
                    is ApiResult.Success -> {
                        _structuredResponse.postValue(result.data)
                        _error.postValue(null)
                    }
                    is ApiResult.Error -> {
                        _error.postValue(result.message)
                    }
                    is ApiResult.Loading -> {
                        // no-op, loading handled by _loading flag
                    }
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun clearContent() {
        _response.value = null
        _structuredResponse.value = null
    }
}
