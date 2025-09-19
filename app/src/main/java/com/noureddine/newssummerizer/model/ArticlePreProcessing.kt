package com.noureddine.newssummerizer.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "Article_Pre_Processing"
    ,indices = [Index(value = ["url"], unique = true)]
)
class ArticlePreProcessing (

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val url: String,
    val source: String,
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String

)



