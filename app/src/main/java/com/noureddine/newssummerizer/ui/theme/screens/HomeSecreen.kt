package com.noureddine.newssummerizer.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.noureddine.newssummerizer.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noureddine.newssummerizer.model.ArticleAfterProcessing


@Composable
fun HomeSecreen(
    textLoading : String = "",
    loading : Boolean = false,
    articles: List<ArticleAfterProcessing>? = listOf(
        ArticleAfterProcessing(0,"","","","","",""),
        ArticleAfterProcessing(1,"","","","","",""),
        ArticleAfterProcessing(2,"","","","","",""),
        ArticleAfterProcessing(3,"","","","","","")
    ),
    onNavigateToDetails: (String) -> Unit
) {

    var isLoading by remember { mutableStateOf(loading) }
    
    // Update isLoading when loading parameter changes
    LaunchedEffect(loading) {
        isLoading = loading
    }

    Column(
        modifier = Modifier
            .background(NewsColors.WarmWhite)
            .statusBarsPadding()
            .fillMaxSize()
    ) {

        Text(
            text = "news summerizer ("+articles?.size.toString()+")",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NewsColors.OrangeAccent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (articles.isNullOrEmpty()) {
            // عرض رسالة عندما لا توجد مقالات
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "لا توجد مقالات متاحة",
                    color = NewsColors.DeepNavy,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(articles.size) { index ->
                    val article = articles[index]

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                            .clickable { onNavigateToDetails(article.id.toString()) }
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxHeight()             // الطول = طول الشاشة
                                .wrapContentWidth(),          // العرض = على قد المحتوى
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = (index+1).toString(),
                                color = NewsColors.OrangeAccent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp) // مسافة حول النص
                                    .fillMaxHeight()             // الطول = طول الشاشة
                                    .wrapContentWidth()          // العرض = على قد المحتوى
                            )

                            val context = LocalContext.current
                            val imageUrl = article.urlToImage

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                                    .build(),
                                contentDescription = article.title ?: "article image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                color = NewsColors.DeepNavy,
                                text = article.title ?: "Title $index",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                color = NewsColors.DeepNavy,
                                text = article.publishedAt ?: "Jan 01, 2024, 12:00 AM",
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    if (index < articles.lastIndex) {
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                } // This closing brace was missing
            }
            }
        }
    }


    if (isLoading) {
        LoadingDialog( textLoading, onDismiss = {
            // Don't allow dismissing the dialog manually during loading
            // The dialog will be hidden when loading becomes false
        })
    }


}

@Composable
fun LoadingDialog(textLoading: String, onDismiss: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(16.dp))
                //Text("Loading...")
                Text(textLoading)
            }
        },
        confirmButton = {}
    )
}


// للمعاينة - Preview function moved to top level
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeSecreenPreview() {
    MaterialTheme { // Wrap with your app's theme if available
        HomeSecreen(articles = listOf(
        ArticleAfterProcessing(0,"","","","","",""),
        ArticleAfterProcessing(1,"","","","","",""),
        ArticleAfterProcessing(2,"","","","","",""),
        ArticleAfterProcessing(3,"","","","","","")
        ), onNavigateToDetails = {})
        //LoadingDialog(onDismiss = {})
    }
}