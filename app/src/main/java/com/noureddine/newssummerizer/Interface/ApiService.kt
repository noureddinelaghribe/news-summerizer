package com.noureddine.newssummerizer.Interface

import com.google.gson.annotations.SerializedName
import com.noureddine.newssummerizer.model.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("v2/everything")
    suspend fun getArticles(
        @Query("q") query: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): ApiResponse

}