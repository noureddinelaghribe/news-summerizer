package com.noureddine.newssummerizer

import android.os.Bundle
import android.util.Log
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.viewModels
import com.noureddine.newssummerizer.ui.theme.NewsSummerizerTheme
import com.noureddine.newssummerizer.navigation.NavigationFromSplashScreentoHomeSecreen
import com.noureddine.newssummerizer.model.ApiRequest
import com.noureddine.newssummerizer.model.ArticleAfterProcessing
import com.noureddine.newssummerizer.model.ArticlePreProcessing
import com.noureddine.newssummerizer.viewModel.newsViewModel
import com.noureddine.newssummerizer.viewModel.newsViewModelRoomDb
import com.noureddine.newssummerizer.viewModel.GeminiViewModel
import com.noureddine.newssummerizer.viewModel.SharedViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.getValue
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {

    private val TAG = "TAG-MainActivity"

    private val viewModel: newsViewModel by viewModels()
    private val viewModelRoomDb: newsViewModelRoomDb by viewModels()
    private val geminiViewModel: GeminiViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var numberRequest = 0
    private var numberRequestSummerized = 0
    private var showNumberRequestSummerized = 0
    private var numberOfTotalRequstApi = 0
    private lateinit var listLinks: List<String>

    val prompt2 = """
# Tech News Article Analysis & Topic Summarization

## System Instructions:
You are an expert technology analyst specializing in AI, Cloud Computing, Cybersecurity, Web Development, iOS, and Android development. Your primary task is to analyze news articles and provide topic-based summaries rather than article summaries.

## Core Focus Areas:
- **AI & Machine Learning**: New models, tools, frameworks, industry developments
- **Cloud Computing**: AWS, Azure, GCP updates, serverless, infrastructure changes  
- **Cybersecurity**: Threats, vulnerabilities, security tools, data breaches, policies
- **Web Development**: New frameworks, browser updates, web standards, developer tools
- **iOS Development**: iOS updates, Swift changes, App Store policies, development tools
- **Android Development**: Android versions, Kotlin updates, Play Store changes, development tools

## Task Instructions:
When given article links, you should:
1. **Extract the core TOPIC/THEME** from each article
2. **Summarize the TOPIC itself**, not just the article content
3. **Provide broader context** about why this topic matters
4. **Focus on actionable insights** for developers and tech professionals

## Filtering Criteria:

### **Include These Topics:**
- New AI models, APIs, or breakthrough technologies
- Major cloud platform updates and new services
- Critical security vulnerabilities and threat reports
- Significant framework or language updates
- Platform policy changes affecting developers
- Major acquisitions impacting tech ecosystem
- Regulatory changes affecting tech industry
- New developer tools and productivity solutions
- Infrastructure and architecture innovations
- Open source project major releases

### **Exclude These Topics:**
- Company earnings without strategic implications
- Personnel changes and executive movements
- Stock price movements without context
- Minor bug fixes or patch releases
- Social media platform drama
- Consumer device reviews
- Marketing announcements without technical substance
- Theoretical research without practical applications

## Output Format:
**Important:** All content must be in Arabic.

Return ONLY valid JSON without markdown formatting:

{
  "articles": [
    {
      "title": "العنوان مترجم للعربية",
      "link": "https://original-article-link.com",
      "text": "ملخص مختصر للمقالة باللغة العربية في 2-3 جمل يسلط الضوء على أهم النقاط والمعلومات الرئيسية"
    }
  ]
}

## Critical JSON Requirements:
- NO markdown code blocks or backticks
- NO internal quotation marks in Arabic text
- Use proper JSON escaping for special characters
- Return only valid JSON structure
- No explanatory text before or after JSON

## Language Guidelines:
- **All content in Arabic** except technical terms commonly used in English
- Keep technical terms like API, SDK, iOS, Android in English when standard
- Use clear, professional Arabic for tech content
- Avoid quotation marks within Arabic text content
- Use Arabic numbers and proper punctuation

## Analysis Approach:
Instead of saying "المقالة تتحدث عن..." say "موضوع الذكاء الاصطناعي في التطوير يشهد تطوراً كبيراً من خلال..."

Focus on the TOPIC'S significance, trends, and implications rather than just what the article reports.

## Processing Steps:
1. Read the article from provided link
2. Identify the core technology topic/theme
3. Analyze the topic's broader significance
4. Determine category (AI/Cloud/Cybersecurity/WebDev/iOS/Android)
5. Assess impact level and target developer audience
6. Create topic-focused summary with actionable insights
7. Return as valid JSON only

Only include articles that meet the technical focus criteria above.
"""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            NewsSummerizerTheme {
                NavigationFromSplashScreentoHomeSecreen(sharedViewModel, viewModelRoomDb)
            }

        }




        sharedPref = getSharedPreferences("NewsTody", MODE_PRIVATE)
        editor = sharedPref.edit()

        if( sharedPref.getString("lastDataLoad","").equals(getData()) ){
            //just load data from room
            loadDataFromRoomDb()
        }else{
            numberRequestSummerized = sharedPref.getInt("numberRequestApi", 0)

            // Clear Room DB and reload data from API
            viewModelRoomDb.deleteAllArticlesPreProcessing()
            sharedViewModel.stateData("Clearing Previous Data", true)
        }



        viewModelRoomDb.responseDeleteAllArticlePreProcessing.observe(this) { response ->
            viewModelRoomDb.deleteAllArticlesAfterProcessing()
        }

        viewModelRoomDb.responseDeleteAllArticleAfterProcessing.observe(this) { response ->
            getNews()
            sharedViewModel.stateData("Start Geting News", true)
        }



        viewModel.loading.observe(this) { isLoading -> }

        viewModel.response.observe(this) { response ->

            val listArticles: List<ArticlePreProcessing> =
                response?.articles.orEmpty().map { a ->
                    ArticlePreProcessing(
                        source = a.source.name,
                        title = a.title.orEmpty(),
                        description = a.description.orEmpty(),
                        url = a.url.orEmpty(),
                        urlToImage = a.urlToImage.orEmpty(),
                        publishedAt = a.publishedAt?.let(::isoToDate).orEmpty(),
                        content = a.content.orEmpty()
                    )
                }

            viewModelRoomDb.addArticlesPreProcessing(listArticles)

        }

        viewModel.error.observe(this) { err ->
            Log.e(TAG, "NEWS viewModel error " + err.toString())
        }

        viewModelRoomDb.responseAddArticlesPreProcessing.observe(this){ list ->
            numberRequest++
            getNews()
        }

        viewModelRoomDb.responseGetArticlesPreProcessing.observe(this){ list ->

            numberOfTotalRequstApi = numberOfTotalRequstApi(list?.size ?: 0)

            var links = ""
            if (list != null){
                for (l in list){
                    links+= l.url+" "
                }
            }

            listLinks = splitTextByWords(links)
            listLinks = listLinks.distinct()

            getNewsSummerized("viewModelRoomDb.responseGetArticlesPreProcessing")
            showNumberRequestSummerized = numberRequestSummerized+1
            sharedViewModel.stateData("Start Summerizing News "+showNumberRequestSummerized+"/"+numberOfTotalRequstApi, true)


        }

        viewModelRoomDb.loading.observe(this){ _ -> }

        viewModelRoomDb.error.observe(this){ _ -> }



        geminiViewModel.loading.observe(this) { isLoading -> }

        geminiViewModel.error.observe(this) { error ->
            if(error != null){
                Log.d(TAG, "Gemini ViewModel error : "+error)

                showNumberRequestSummerized = numberRequestSummerized+1
                sharedViewModel.stateData("Start Summerizing News Recall "+showNumberRequestSummerized+"/"+numberOfTotalRequstApi, true)

                lifecycleScope.launch {
                    delay(70_000)
                    getNewsSummerized("geminiViewModel.error")
                }
            }
        }

        geminiViewModel.structuredResponse.observe(this) { response ->
            Log.d(TAG, "structuredResponse")
            if (response != null){
                Log.d(TAG, "geminiViewModel size : "+response.articles.size)

                if (response.articles.size>0){
                    for (a in response.articles){
                        Log.d(TAG, "geminiViewModel title : "+a.title)
                        Log.d(TAG, "geminiViewModel text : "+a.text)
                        Log.d(TAG, "geminiViewModel link : "+a.link)
                        val articleAfterProcessing = ArticleAfterProcessing(
                            source = "",
                            title = a.title,
                            text = a.text,
                            url = a.link,
                            urlToImage = "",
                            publishedAt = ""
                        )
                        viewModelRoomDb.addArticleAfterProcessing(articleAfterProcessing)
                    }
                }else{

                    showNumberRequestSummerized = numberRequestSummerized+1
                    sharedViewModel.stateData("Start Summerizing News "+showNumberRequestSummerized+"/"+numberOfTotalRequstApi, true)

                    lifecycleScope.launch {
                        editor.putInt("numberRequestApi", numberRequestSummerized)
                        editor.apply()
                        delay(10_000)
                        numberRequestSummerized++
                        getNewsSummerized("structuredResponse response.articles.size=0")
                    }

                }

            }else{
                Log.d(TAG, "Structured Response is null")
            }

        }

        viewModelRoomDb.responseAddArticleAfterProcessing.observe(this){ response ->
            if (response != null){
                Log.d(TAG, "responseAddArticleAfterProcessing")
                viewModelRoomDb.getArticlePreProcessing(response.url)
            }else{
                Log.e(TAG, "onCreate: Response Add Articles After Processing is null")
            }
        }


        viewModelRoomDb.responseGetArticlePreProcessing.observe(this){ response ->
            if (response != null){
                Log.d(TAG, "responseGetArticlePreProcessing")
                val articleAfterProcessing = ArticleAfterProcessing(
                    source = response.source,
                    title = "",
                    text = "",
                    url = response.url,
                    urlToImage = response.urlToImage,
                    publishedAt = response.publishedAt
                )
                viewModelRoomDb.updateArticleAfterProcessing(articleAfterProcessing)
            }else{
                Log.e(TAG, "onCreate: Response Get Article Pre Processing is null")
            }
        }


        viewModelRoomDb.responseUpdateArticleAfterProcessing.observe(this) { response ->
            if (response != null){
                Log.d(TAG, "responseUpdateArticleAfterProcessing "+response)
                //viewModelRoomDb.deleteArticleByUrl(response.url)

                showNumberRequestSummerized = numberRequestSummerized+1
                sharedViewModel.stateData("Start Summerizing News "+showNumberRequestSummerized+"/"+numberOfTotalRequstApi, true)

                lifecycleScope.launch {
                    editor.putInt("numberRequestApi", numberRequestSummerized)
                    editor.apply()
                    delay(10_000)
                    numberRequestSummerized++
                    getNewsSummerized("viewModelRoomDb.responseUpdateArticleAfterProcessing")
                }

            }else{
                Log.e(TAG, "onCreate: Response Update Article After Processing is null")
            }
        }







    }


    fun getNews(){

        val androidKeywords = "android OR kotlin OR \"android development\" OR \"mobile development\" OR \"app development\" OR \"android studio\" OR gradle OR jetpack OR compose OR \"android sdk\" OR \"play store\" OR \"google play\" OR fragments OR activities OR recyclerview OR retrofit OR room OR dagger OR hilt OR coroutines OR \"material design\" OR \"android architecture\""
        val iOSKeywords = "iOS OR swift OR xcode OR \"iOS development\" OR \"app store\" OR objective-c OR swiftui OR uikit OR cocoapods OR carthage OR \"swift package manager\" OR core data OR \"ios sdk\" OR \"apple developer\" OR testflight OR \"ios app\" OR iphone OR ipad OR watchOS OR tvOS OR macOS"
        val webDevKeywords = "javascript OR typescript OR \"web development\" OR react OR angular OR vue OR node.js OR express OR html OR css OR \"frontend development\" OR \"backend development\" OR \"full stack\" OR webpack OR npm OR yarn OR \"responsive design\" OR bootstrap OR tailwind OR sass OR less OR \"web frameworks\" OR \"web technologies\""
        val fintechKeywords = "fintech OR \"financial technology\" OR neobank OR \"digital payments\" OR \"mobile payments\" OR paypal OR stripe OR square OR klarna OR afterpay OR \"contactless payment\" OR \"digital wallet\" OR banking OR \"online banking\" OR \"payment processing\" OR \"financial services\" OR \"digital finance\" OR \"payment gateway\" OR \"buy now pay later\" OR BNPL"
        val startupKeywords = "startup OR entrepreneurship OR \"startup funding\" OR \"venture capital\" OR \"series A\" OR \"series B\" OR unicorn OR IPO OR \"business strategy\" OR \"small business\" OR innovation OR \"tech startup\" OR investor OR funding OR valuation OR acquisition OR merger OR \"startup news\" OR \"entrepreneur\" OR \"business model\" OR scaling"
        val cybersecurityKeywords = "cybersecurity OR \"cyber security\" OR \"data breach\" OR hacking OR malware OR ransomware OR \"security breach\" OR encryption OR \"two factor authentication\" OR biometrics OR \"identity verification\" OR \"fraud detection\" OR \"risk management\" OR \"cyber attack\" OR \"network security\" OR \"information security\" OR firewall OR antivirus OR \"security tools\""
        val cloudKeywords = "\"cloud computing\" OR AWS OR azure OR \"google cloud\" OR DevOps OR docker OR kubernetes OR \"CI/CD\" OR serverless OR microservices OR containers OR automation OR \"infrastructure as code\" OR terraform OR ansible OR jenkins OR \"cloud services\" OR \"cloud migration\" OR \"cloud security\" OR \"cloud architecture\""
        val blockchainKeywords = "blockchain OR cryptocurrency OR bitcoin OR ethereum OR solana OR polygon OR cardano OR \"smart contracts\" OR DeFi OR NFT OR \"web3\" OR \"decentralized finance\" OR \"crypto trading\" OR \"digital currency\" OR \"blockchain technology\" OR \"crypto market\" OR \"cryptocurrency news\" OR binance OR coinbase OR \"crypto wallet\" OR mining OR staking"
        val aiKeywords = "\"artificial intelligence\" OR \"machine learning\" OR \"deep learning\" OR AI OR ML OR \"neural networks\" OR tensorflow OR pytorch OR \"data science\" OR \"computer vision\" OR NLP OR \"natural language processing\" OR GPT OR ChatGPT OR OpenAI OR \"large language model\" OR LLM OR \"generative AI\" OR algorithms OR \"predictive analytics\" OR \"machine learning ops\" OR MLOps"

        val listKeywords = listOf<String>(
            androidKeywords,iOSKeywords, webDevKeywords, aiKeywords,cybersecurityKeywords,cloudKeywords
            //, startupKeywords,blockchainKeywords
        )

        if (numberRequest<=listKeywords.size-1){
            val apiRequest = ApiRequest(
                query = listKeywords.get(numberRequest),
                from = getData(),
                to = getData(),
                language = "en",
                sortBy = "publishedAt",
                pageSize = 100
            )

            viewModel.getNews(apiRequest)

        } else {
            Log.d(TAG, "All API calls completed for ${listKeywords.size} keyword groups")

            viewModelRoomDb.getAllArticlePreProcessing()

        }

    }

    fun getData(): String{
        // Get yesterday's date
        val yesterday = LocalDate.now().minusDays(1)

        // Format as yyyy-MM-dd
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = yesterday.format(formatter)

        return formatted
    }

    fun isoToDate(iso: String): String {

        // 1) Parse the ISO instant you gave
        //val iso = "2025-09-07T23:02:22Z"
        val odt = OffsetDateTime.parse(iso) // parses the Z (UTC)

        // Choose French locale and pattern "yyyy, MMM d"
        val fmt = DateTimeFormatter.ofPattern("yyyy, MMM d", Locale.FRENCH)

        // Format the parsed timestamp (keeps the same date in UTC; convert zone if needed)
        val formattedFromIso = odt.format(fmt)          // e.g. "2025, sept. 7"
        val formattedFromIsoNoDot = formattedFromIso.replace(".", "") // e.g. "2025, sept 7"

        return formattedFromIsoNoDot
    }

    fun splitTextByWords(text: String, chunkSize: Int = 10): List<String> {
        val words = text.split("\\s+".toRegex()) // يقسم النص حسب المسافات
        val chunks = mutableListOf<String>()

        var currentChunk = StringBuilder()
        var count = 0

        for (word in words) {
            if (count >= chunkSize) {
                chunks.add(currentChunk.toString().trim())
                currentChunk = StringBuilder()
                count = 0
            }
            currentChunk.append(word).append(" ")
            count++
        }

        // أضف آخر جزء إذا لم يكن فارغ
        if (currentChunk.isNotBlank()) {
            chunks.add(currentChunk.toString().trim())
        }

        return chunks
    }


    fun getNewsSummerized(text: String){

        Log.d(TAG, "getNewsSummerized numberRequestSummerized : "+numberRequestSummerized+" from : "+text)

        if (numberRequestSummerized<=listLinks.size-1){

            geminiViewModel.generateStructuredContent(listLinks.get(numberRequestSummerized).trimIndent(),prompt2)

        } else {
            Log.d(TAG, "All API calls completed for ${listLinks.size} news summerized")
            editor.putString("lastDataLoad", getData())
            editor.putInt("numberRequestApi", 0)
            editor.apply()
            loadDataFromRoomDb()
        }

    }


    fun loadDataFromRoomDb() {
        // Load all articles after processing from Room database
        viewModelRoomDb.getAllArticleAfterProcessing()
        sharedViewModel.stateData("All Summaries Completed", false)
    }

    fun numberOfTotalRequstApi(allArticles: Int): Int {
        return (allArticles + 9) / 10
    }











}