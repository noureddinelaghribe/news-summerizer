package com.noureddine.newssummerizer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noureddine.newssummerizer.model.ApiRequest
import com.noureddine.newssummerizer.model.ApiResponse
import com.noureddine.newssummerizer.repository.newsRepository
import kotlinx.coroutines.launch

class newsViewModel : ViewModel() {

    private val repository = newsRepository()

    private val _response = MutableLiveData<ApiResponse?>()
    val response: LiveData<ApiResponse?> = _response

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun getNews(apiRequest: ApiRequest) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val result = repository.getNews(apiRequest)
                _response.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

}