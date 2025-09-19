package com.noureddine.newssummerizer.repository

import com.noureddine.newssummerizer.database.AppDatabase
import com.noureddine.newssummerizer.model.ArticleAfterProcessing
import com.noureddine.newssummerizer.model.ArticlePreProcessing
import kotlinx.coroutines.flow.Flow

class newsRepositoryRoomDb(private val db: AppDatabase) {

    suspend fun addArticlePreProcessing(articlePreProcessing: ArticlePreProcessing) {
        db.articleDao().insertArticlePreProcessing(articlePreProcessing)
    }

    suspend fun addArticlePresProcessing(articles: List<ArticlePreProcessing>) {
        db.articleDao().insertArticlesPreProcessing(articles)
    }

    suspend fun getAllArticlePreProcessing(): List<ArticlePreProcessing> {
        return db.articleDao().getAllArticlePreProcessing()
    }

    suspend fun getArticlePreProcessing(url: String): ArticlePreProcessing {
        return db.articleDao().getArticlePreProcessing(url)
    }

    fun observeAllArticlePreProcessing(): Flow<List<ArticlePreProcessing>> {
        return db.articleDao().observeAllArticlePreProcessing()
    }

    fun deleteArticleByUrl(url: String): Int {
        return db.articleDao().deleteArticleByUrl(url)
    }

    fun deleteArticlesByUrls(urls: MutableList<String?>?) {
        db.articleDao().deleteArticlesByUrls(urls)
    }

    suspend fun deleteAllArticlesPreProcessing(): Int {
        return db.articleDao().deleteAllArticlesPreProcessing()
    }





    suspend fun addArticleAfterProcessing(articleAfterProcessing: ArticleAfterProcessing): Long {
        return db.articleDao().insertArticleAfterProcessing(articleAfterProcessing)
    }

    suspend fun addArticlesAfterProcessing(articles: List<ArticleAfterProcessing>) {
        db.articleDao().insertArticlesAfterProcessing(articles)
    }

    suspend fun getAllArticleAfterProcessing(): List<ArticleAfterProcessing> {
        return db.articleDao().getAllArticleAfterProcessing()
    }

    suspend fun updateinsertArticleAfterProcessing(article: ArticleAfterProcessing): Int {
        return db.articleDao().updateArticleAfterProcessing(
            article.source,
            article.urlToImage,
            article.publishedAt,
            article.url
        )
    }

    suspend fun deleteAllArticlesAfterProcessing(): Int {
        return db.articleDao().deleteAllArticlesAfterProcessing()
    }



}