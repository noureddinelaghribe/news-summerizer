# News Summerizer - Quick News Summaries

Welcome to the **News Summerizer** app! This Android application fetches news articles and produces short, readable summaries so you can stay informed quickly. Built with Kotlin and Android Studio, News Summerizer is a lightweight tool for readers who want fast, scannable news.

## Download

<p align="center">
    <a href="https://github.com/noureddinelaghribe/news-summerizer/raw/refs/heads/main/News-Summerizer.apk">
        <img src="https://raw.githubusercontent.com/noureddinelaghribe/WriteFlow/refs/heads/main/download_apk.png" alt="Download APK" height="80">
    </a>
</p>

> If an APK is not provided in the repository, build the app from source (see **Run the app** section below).

## Screenshots

Here are some screenshots of the News Summerizer app:

| Screenshot 1                                                                                                      | Screenshot 2                                                                                                      | Screenshot 3                                                                                                      |
| ----------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| ![Screenshot 1](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_1.jpg) | ![Screenshot 2](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_2.jpg) | ![Screenshot 3](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_3.jpg) |

| Screenshot 4                                                                                                      | Screenshot 5                                                                                                      | Screenshot 6                                                                                                      |
| ----------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| ![Screenshot 4](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_4.jpg) | ![Screenshot 5](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_5.jpg) | ![Screenshot 6](https://raw.githubusercontent.com/noureddinelaghribe/news-summerizer/refs/heads/main/photo_6.jpg) |

> Replace the image links above with the correct screenshot filenames from your repo if they differ.

## Features

* **Headlines List:** Browse latest headlines from configured news sources.
* **Article View:** Read full articles inside the app.
* **Automatic Summaries:** Generate short, clear summaries of articles for quick reading.
* **Save / Bookmark:** Save important articles or summaries for later (optional).
* **Share:** Share article links or generated summaries via other apps.

## Technologies Used

* **Kotlin:** Main programming language for the app.
* **Android Studio:** IDE used to build the project.
* **Retrofit / OkHttp:** Network requests and API communication.
* **Coroutines / Flow:** Asynchronous operations and data streams.
* **Jetpack (ViewModel, LiveData/StateFlow):** App architecture and lifecycle-aware components.
* **Optional:** A remote summarization API or on-device summarizer (configure in settings).

## Architecture

### MVC (Model-View-Controller)

This project follows a simple **MVC** pattern to keep responsibilities clear and the code easy to follow:

* **Model:** Contains data classes for articles, summaries, and local storage handling. Interacts with remote news APIs and any summarization services.
* **View:** Activities and fragments that render the UI: headlines list, article detail, and summary view.
* **Controller:** Handles user interactions, coordinates fetching articles, creating summaries, and updating the view. Controllers also manage navigation and share/save actions.

> You can migrate to **MVVM** easily by introducing `ViewModel` classes and `StateFlow`/`LiveData` for better separation when the app grows.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For technical support or feature requests, please contact:

*\[[noureddinne.office@gmail.com](mailto:noureddinne.office@gmail.com)]*

---

*This README was formatted to match the style of your Sweet Store README and tailored for the News Summerizer project. Update the download/screenshot links and configuration placeholders as needed.*
