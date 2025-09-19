package com.noureddine.newssummerizer.model

data class ApiRequest(
    val query: String ,
    val from: String ,
    val to: String ,
    val language: String ,
    val sortBy: String ,
    val pageSize: Int
    //,
    //val domains: String
)
