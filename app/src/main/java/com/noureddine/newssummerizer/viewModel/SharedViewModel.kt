package com.noureddine.newssummerizer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _responseData = MutableLiveData<String?>()
    val responseData: LiveData<String?> = _responseData

    private val _loadingData = MutableLiveData<Boolean>()
    val loadingData: LiveData<Boolean> = _loadingData

    private val _errorData = MutableLiveData<String?>()
    val errorData: LiveData<String?> = _errorData

    fun stateData(text: String, loading: Boolean) {
        viewModelScope.launch {
            _loadingData.postValue(loading)
            try {
                _responseData.postValue(text)
            } catch (e: Exception) {
                _errorData.postValue(e.message)
            } finally {
                _loadingData.postValue(loading)
            }
        }
    }
}
