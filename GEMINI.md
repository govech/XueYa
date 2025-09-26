# Project: XueYa (血压记录应用)

## Project Overview

XueYa is an Android application for recording and analyzing blood pressure data. It is built using modern Android development technologies and follows a clean architecture pattern. The app allows users to log their blood pressure readings, view historical data, and receive AI-powered health advice.

**Key Technologies:**

*   **Language:** Kotlin
*   **UI:** Jetpack Compose with Material Design 3
*   **Architecture:** MVVM with Clean Architecture
*   **Asynchronous:** Kotlin Coroutines and Flow
*   **Database:** Room
*   **Networking:** Retrofit and OkHttp
*   **Dependency Injection:** Hilt
*   **Data Storage:** DataStore
*   **AI:** OpenRouter API (DeepSeek V3)
*   **Speech Recognition:** Android Speech API

## Building and Running

### Build

To build the project, run the following command in the root directory:

```bash
./gradlew assembleDebug
```

### Test

To run the unit tests, use the following command:

```bash
./gradlew test
```

### API Keys

The project uses the OpenRouter API for its AI features. To use these features, you need to provide an API key. Create a `local.properties` file in the root directory of the project and add the following line:

```properties
OPENROUTER_API_KEY=your_actual_api_key_here
```

## Development Conventions

*   **Architecture:** The project follows the principles of Clean Architecture, separating the code into three layers: presentation, domain, and data.
*   **UI:** The UI is built with Jetpack Compose, and UI components are organized by screen in the `presentation/screens` directory.
*   **State Management:** ViewModels are used to manage the state of the UI, exposing data to the UI using `StateFlow`.
*   **Dependency Injection:** Hilt is used for dependency injection to manage the dependencies between different parts of the application.
*   **Testing:** Unit tests are located in the `app/src/test` directory.
