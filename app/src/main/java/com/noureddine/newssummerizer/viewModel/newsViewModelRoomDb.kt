package com.noureddine.newssummerizer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noureddine.newssummerizer.database.AppDatabase
import com.noureddine.newssummerizer.model.ArticleAfterProcessing
import com.noureddine.newssummerizer.model.ArticlePreProcessing
import com.noureddine.newssummerizer.repository.newsRepositoryRoomDb
import kotlinx.coroutines.launch

class newsViewModelRoomDb(application: Application) : AndroidViewModel(application) {

    private val repository = newsRepositoryRoomDb(AppDatabase.getDatabase(getApplication()))

    private val _responseAddArticlesPreProcessing = MutableLiveData<List<ArticlePreProcessing>?>()
    val responseAddArticlesPreProcessing: LiveData<List<ArticlePreProcessing>?> = _responseAddArticlesPreProcessing

    private val _responseGetArticlesPreProcessing = MutableLiveData<List<ArticlePreProcessing>?>()
    val responseGetArticlesPreProcessing: LiveData<List<ArticlePreProcessing>?> = _responseGetArticlesPreProcessing

    private val _responseGetArticlePreProcessing = MutableLiveData<ArticlePreProcessing?>()
    val responseGetArticlePreProcessing: LiveData<ArticlePreProcessing?> = _responseGetArticlePreProcessing

    val articlesPreProcessing: LiveData<List<ArticlePreProcessing>> =
        repository.observeAllArticlePreProcessing().asLiveData()

    private val _responseDeleteArticlePreProcessing = MutableLiveData<Int?>()
    val responseDeleteArticlePreProcessing: LiveData<Int?> = _responseDeleteArticlePreProcessing

    private val _responseDeleteAllArticlePreProcessing = MutableLiveData<Int?>()
    val responseDeleteAllArticlePreProcessing: LiveData<Int?> = _responseDeleteAllArticlePreProcessing





    private val _responseAddArticleAfterProcessing = MutableLiveData<ArticleAfterProcessing?>()
    val responseAddArticleAfterProcessing: LiveData<ArticleAfterProcessing?> = _responseAddArticleAfterProcessing

    private val _responseAddArticlesAfterProcessing = MutableLiveData<List<ArticleAfterProcessing>?>()
    val responseAddArticlesAfterProcessing: LiveData<List<ArticleAfterProcessing>?> = _responseAddArticlesAfterProcessing

    private val _responseGetArticlesAfterProcessing = MutableLiveData<List<ArticleAfterProcessing>?>()
    val responseGetArticlesAfterProcessing: LiveData<List<ArticleAfterProcessing>?> = _responseGetArticlesAfterProcessing

    private val _responseUpdateArticleAfterProcessing = MutableLiveData<Int?>()
    val responseUpdateArticleAfterProcessing: LiveData<Int?> = _responseUpdateArticleAfterProcessing

    private val _responseDeleteAllArticleAfterProcessing = MutableLiveData<Int?>()
    val responseDeleteAllArticleAfterProcessing: LiveData<Int?> = _responseDeleteAllArticleAfterProcessing




    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

//    fun addArticlePreProcessing(articlePreProcessing: ArticlePreProcessing) {
//        viewModelScope.launch {
//            _loading.postValue(true)
//            try {
//                repository.addArticlePreProcessing(articlePreProcessing)
//                _responseArticlePreProcessing.postValue(articlePreProcessing)
//            } catch (e: Exception) {
//                _error.postValue(e.message)
//            } finally {
//                _loading.postValue(false)
//            }
//        }
//    }

    fun addArticlesPreProcessing(articles: List<ArticlePreProcessing>) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                repository.addArticlePresProcessing(articles)
                _responseAddArticlesPreProcessing.postValue(articles)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getAllArticlePreProcessing(){
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val list = repository.getAllArticlePreProcessing()
                _responseGetArticlesPreProcessing.postValue(list)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getArticlePreProcessing(url: String){
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val article = repository.getArticlePreProcessing(url)
                _responseGetArticlePreProcessing.postValue(article)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun deleteArticleByUrl(url: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val rowsDeleted = repository.deleteArticleByUrl(url)
                _responseDeleteArticlePreProcessing.postValue(rowsDeleted)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun deleteArticlesByUrls(urls: MutableList<String?>?) {
        viewModelScope.launch {
            repository.deleteArticlesByUrls(urls)
        }
    }

    fun deleteAllArticlesPreProcessing() {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val rowsDeleted = repository.deleteAllArticlesPreProcessing()
                _responseDeleteAllArticlePreProcessing.postValue(rowsDeleted)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }







    fun addArticleAfterProcessing(articleAfterProcessing: ArticleAfterProcessing) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val article = repository.addArticleAfterProcessing(articleAfterProcessing)
                _responseAddArticleAfterProcessing.postValue(articleAfterProcessing)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun addArticlesAfterProcessing(articleAfterProcessing: List<ArticleAfterProcessing>) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                repository.addArticlesAfterProcessing(articleAfterProcessing)
                _responseAddArticlesAfterProcessing.postValue(articleAfterProcessing)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getAllArticleAfterProcessing(){
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val list = repository.getAllArticleAfterProcessing()
                _responseGetArticlesAfterProcessing.postValue(list)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun updateArticleAfterProcessing(article: ArticleAfterProcessing){
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val updatedArticle = repository.updateinsertArticleAfterProcessing(article)
                _responseUpdateArticleAfterProcessing.postValue(updatedArticle)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun deleteAllArticlesAfterProcessing() {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val rowsDeleted = repository.deleteAllArticlesAfterProcessing()
                _responseDeleteAllArticleAfterProcessing.postValue(rowsDeleted)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }








}