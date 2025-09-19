package com.noureddine.newssummerizer.repository

import com.noureddine.newssummerizer.Utel.AESEncryptor
import com.noureddine.newssummerizer.model.ApiRequest
import com.noureddine.newssummerizer.model.ApiResponse


class newsRepository {

    private val api = RetrofitInstance.api
    //private val apiKey = AESEncryptor.quickDecrypt("9BcKpQtiJUf8WYPU5QCjkbyPD+P2uMCnTX1eUsurn4SBRY84FqKXfDdV/gcLB66Sdi/c8bxJIK2O2zKsSlMUYQ==")
    private val apiKey = AESEncryptor.quickDecrypt("wtMTLeS9DmFClSRCyKPuVmnL5CxijvBLeK2BPD7y7VjD1c/ytV+p9kcpubREZVqP9ndGXbRv8XgUqGtc2Cg6GA==")



    suspend fun getNews(apiRequest: ApiRequest): ApiResponse{

        return api.getArticles(
            query = apiRequest.query,
            from = apiRequest.from,
            to = apiRequest.to,
            language = apiRequest.language,
            sortBy = apiRequest.sortBy,
            pageSize = apiRequest.pageSize,
            //domains = apiRequest.domains,
            apiKey = apiKey
        )
    }

}