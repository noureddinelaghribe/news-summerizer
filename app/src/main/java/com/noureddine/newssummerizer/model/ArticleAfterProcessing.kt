//package com.noureddine.newssummerizer.model
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//
//@Entity(tableName = "Article_After_Processing")
//class ArticleAfterProcessing (
//
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//
//    var url: String,
//    var source: String,
//    var title: String,
//    var text: String,
//    var urlToImage: String,
//    var publishedAt: String
//) {
//    companion object {
//        fun empty() = ArticleAfterProcessing(
//            url = "",
//            source = "",
//            title = "",
//            text = "",
//            urlToImage = "",
//            publishedAt = ""
//        )
//    }
//}



package com.noureddine.newssummerizer.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Article_After_Processing" ,
    indices = [Index(value = ["url"], unique = true)]
)
data class ArticleAfterProcessing(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var url: String,
    var source: String,
    var title: String,
    var text: String,
    var urlToImage: String,
    var publishedAt: String
) {
    companion object {
        fun empty() = ArticleAfterProcessing(
            url = "",
            source = "",
            title = "",
            text = "",
            urlToImage = "",
            publishedAt = ""
        )
    }
}
