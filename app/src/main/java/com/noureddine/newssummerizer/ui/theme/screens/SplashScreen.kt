package com.noureddine.newssummerizer.ui.theme.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

object NewsColors {
    val DeepNavy = Color(0xFF1E293B)
    val WarmWhite = Color(0xFFFEFEFE)
    val SoftBlue = Color(0xFF3B82F6)
    val OrangeAccent = Color(0xFFEA580C)
    val MutedGray = Color(0xFF64748B)
}

@Composable
fun SplashScreen (onSplashFinished: () -> Unit){

    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Column (
        modifier = Modifier.fillMaxSize()
            .background(NewsColors.WarmWhite)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "news summerizer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NewsColors.OrangeAccent
        )

    }

}



// للمعاينة
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme { // Wrap with your app's theme if available
        SplashScreen(onSplashFinished = {})
    }
}
