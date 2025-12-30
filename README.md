# Chatbot

A real-time chatbot application for Android, built with modern Android development tools and best practices. The app facilitates instant two-way communication, featuring a sleek user interface and a robust architecture. It uses a WebSocket for real-time messaging and is structured to be scalable and maintainable.

## Features

- **Real-time Messaging**: Instant two-way conversation with a WebSocket-based backend.
- **Modern UI**: A responsive and intuitive user interface built with Jetpack Compose and Material Design 3.
- **Offline Support**: A message queue system ensures that messages are sent and received reliably, even with intermittent network connectivity.
- **Clean Architecture**: Follows MVVM architecture with a clear separation of concerns, making the codebase clean, scalable, and easy to maintain.
- **Dependency Injection**: Hilt is used for dependency injection, simplifying the management of dependencies and improving testability.
- **Zero Data Loss**: Ensures that all messages are delivered in the correct order.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: OkHttp for WebSocket communication
- **Real-time Backend**: PieSocket
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36

## Architecture

The app follows MVVM architecture with a clear separation of concerns:

```
app/src/main/java/com/example/chatbot/
├── data/
│   ├── repository/     # Data repository layer
│   └── socket/         # WebSocket client
├── domain/
│   ├── model/          # Data models
│   └── repository/     # Domain repository interfaces
├── ui/
│   └── theme/          # Compose UI theme
├── presentation/       # ViewModels
├── di/                 # Dependency injection modules
└── utils/              # Utility classes
```

## Key Components

### PieSocketClient
A WebSocket client that manages the connection to the PieSocket backend. It:
- Establishes and maintains the WebSocket connection
- Listens for incoming messages
- Sends outgoing messages

### ChatRepository
Manages all chat-related data operations:
- Sends and receives messages through the WebSocket client
- Manages the message queue for offline support

### ChatViewModel
Exposes the chat state to the UI and handles user interactions:
- Gets messages from the repository
- Sends new messages from the user

## How It Works

1.  **Establish Connection**: The app connects to the PieSocket backend via a WebSocket.
2.  **Send Message**: The user types a message and sends it. The message is added to a queue and sent to the backend via the WebSocket.
3.  **Receive Message**: The app listens for incoming messages from the WebSocket and displays them in the chat UI.
4.  **Offline Support**: If the app is offline, outgoing messages are queued and sent when the connection is re-established.

## Project Structure

```
chatbot/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/chatbot/
│   │   │   │   ├── data/              # Data layer
│   │   │   │   ├── domain/            # Domain layer
│   │   │   │   ├── ui/                # UI layer
│   │   │   │   ├── presentation/      # Presentation layer (ViewModels)
│   │   │   │   ├── di/                # Dependency injection
│   │   │   │   └── utils/             # Utilities
│   │   │   ├── res/                   # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                      # Unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Dependency versions
├── build.gradle.kts
└── README.md
```

## Demo
* https://drive.google.com/file/d/1RkHd0oxGELonGypM7Yr0nsl5407HrKXH/view?usp=sharing

## APK
* https://drive.google.com/file/d/13E10xx0l72vcqAlRPAUiu-Ojn78Mvqyd/view?usp=sharing
  
