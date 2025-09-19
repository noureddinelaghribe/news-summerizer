package com.noureddine.newssummerizer.Interface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noureddine.newssummerizer.model.ArticleAfterProcessing
import com.noureddine.newssummerizer.model.ArticlePreProcessing
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticlePreProcessing(articlePreProcessing: ArticlePreProcessing)

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //@Insert
    suspend fun insertArticlesPreProcessing(articles: List<ArticlePreProcessing>)

    @Query("SELECT * FROM Article_Pre_Processing")
    suspend fun getAllArticlePreProcessing(): List<ArticlePreProcessing>

    @Query("SELECT * FROM Article_Pre_Processing WHERE url = :url")
    suspend fun getArticlePreProcessing(url: String): ArticlePreProcessing

    @Query("SELECT * FROM Article_Pre_Processing ORDER BY publishedAt DESC")
    fun observeAllArticlePreProcessing(): Flow<List<ArticlePreProcessing>>

    @Query("DELETE FROM Article_Pre_Processing WHERE url = :url")
    fun deleteArticleByUrl(url: String): Int

    @Query("DELETE FROM Article_Pre_Processing WHERE url IN (:urls)")
    fun deleteArticlesByUrls(urls: MutableList<String?>?)

    @Query("DELETE FROM Article_Pre_Processing")
    suspend fun deleteAllArticlesPreProcessing(): Int












    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticleAfterProcessing(articleAfterProcessing: ArticleAfterProcessing): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticlesAfterProcessing(articles: List<ArticleAfterProcessing>)

    @Query("SELECT * FROM Article_After_Processing")
    suspend fun getAllArticleAfterProcessing(): List<ArticleAfterProcessing>

    //@Update
    @Query("""
        UPDATE Article_After_Processing 
        SET source = :source, 
            urlToImage = :urlToImage, 
            publishedAt = :publishedAt
        WHERE url = :url
    """)
    suspend fun updateArticleAfterProcessing(source: String, urlToImage: String, publishedAt: String, url: String): Int

    @Query("DELETE FROM Article_After_Processing")
    suspend fun deleteAllArticlesAfterProcessing(): Int


}