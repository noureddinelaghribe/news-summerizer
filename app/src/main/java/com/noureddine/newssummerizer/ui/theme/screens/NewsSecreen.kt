package com.noureddine.newssummerizer.ui.theme.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noureddine.newssummerizer.R
import com.noureddine.newssummerizer.model.ArticleAfterProcessing


@Composable
fun NewsSecreen(
    articleAfterProcessing: ArticleAfterProcessing?,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Column (
            modifier = Modifier
                .fillMaxSize()
            .background(NewsColors.WarmWhite)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "رجوع",
                color = NewsColors.OrangeAccent,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onNavigateBack() }
            )
        }

        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(articleAfterProcessing?.urlToImage)
                .crossfade(true)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .build(),
            contentDescription = articleAfterProcessing?.title ?: "صورة المقال",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            color = NewsColors.DeepNavy,
            text = articleAfterProcessing?.title ?: "",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Source label
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = NewsColors.MutedGray,
            text = (if ((articleAfterProcessing?.source ?: "").isNotEmpty()) "المصدر: ${articleAfterProcessing?.source}" else "")
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            color = NewsColors.DeepNavy,
            text = articleAfterProcessing?.publishedAt ?: ""
        )

        Spacer(modifier = Modifier.height(8.dp))

        val bodyText = articleAfterProcessing?.text ?: ""
        if (bodyText.isEmpty() && articleAfterProcessing == null) {
            Text(
                modifier = Modifier.fillMaxSize(),
                color = NewsColors.MutedGray,
                text = "المقال غير موجود",
                textAlign = TextAlign.Start
            )
        } else {
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = NewsColors.MutedGray,
                text = bodyText,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //copy result
        Button(
            onClick = {

                val text = articleAfterProcessing?.title +
                        "\n" +articleAfterProcessing?.text +
                        "\n" +articleAfterProcessing?.url

                val clip = ClipData.newPlainText("label", text)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "تم نسخ النص ✅", Toast.LENGTH_SHORT).show()

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "نسخ الخبر",
                color = NewsColors.WarmWhite,
                fontSize = 16.sp
            )
        }

        // Open URL button
        Button(
            onClick = {
                val url = articleAfterProcessing?.url
                if (!url.isNullOrBlank()) uriHandler.openUri(url)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "اذهب إلى الخبر",
                color = NewsColors.WarmWhite,
                fontSize = 16.sp
            )
        }

        }
    }

}

// للمعاينة
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NewsSecreenPreview() {
    MaterialTheme { // Wrap with your app's theme if available
        NewsSecreen(articleAfterProcessing = null, onNavigateBack = {})
    }
}