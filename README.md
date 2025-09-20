# KirCE Logger

![KirCE Logger Logo](images/logo.png)

**KirCE Logger** is an open-source, high-performance Java logger library.  
It is **cross-platform**, working seamlessly on **Android**, **Desktop**, and **Server** applications.  
Designed for developers who need **flexible, extensible, and color-coded logging**.

---

## Features

- Cross-platform: Android, Desktop, and Server
- Supports multiple log levels:
  - `TRACE` – fine-grained debug information
  - `DEBUG` – general debug info
  - `INFO` – informational messages
  - `WARN` – warnings for potential issues
  - `ERROR` – recoverable errors
  - `FATAL` – severe errors likely causing termination
- Optional **ANSI color codes** for console output
- Asynchronous logging support for high performance
- Extensible via custom `LogHandler`s
- Memory-based log storage for later analysis
- HTTP log submission for remote servers
- Customizable log formatting with `LogFormatter`

---

## Installation

### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.mixofficial:KirCE-Logger:+'
}
```

## Quick Start

```java
import mx.kirce.logger.KirCELogger;
import mx.kirce.logger.handle.ConsoleLogHandler;
import mx.kirce.logger.LogLevel;

public class Main {
    public static void main(String[] args) {
        KirCELogger logger = new KirCELogger("Main");
        logger.addHandler(new ConsoleLogHandler(true)); // Enable colors
        KirCELogger.setGlobalMinLevel(LogLevel.INFO);

        logger.trace("TRACE message"); // won't be shown
        logger.debug("DEBUG message"); // won't be shown
        logger.info("INFO message");
        logger.warn("WARN message");
        logger.error("ERROR message");
        logger.fatal("FATAL message");
    }
}
```

## Log Levels and Colors

*Level*	*Color*	*Description*

`TRACE`	Blue	Fine-grained debug info
`DEBUG`	Cyan	General debugging info
`INFO`	Green	Informational messages
`WARN`	Yellow	Warnings for potential `issues`
`ERROR`	Red	Recoverable errors
`FATAL`	Magenta	Severe errors likely causing termination

## Advanced Features

- **MemoryLogHandler** - – store last N messages in memory

- **HttpLogHandler** - – send log messages to a server

- **Asynchronous logging** – log messages without blocking the main thread.  
  Example: `logger.infoAsync("message")`

- **Custom log formatting** – create a `LogFormatter` to define your own log message template.  

```java
LogFormatter formatter = new LogFormatter("HH:mm:ss", "{time} | {level} | {tag}: {message}");
```

- **Color control** – enable/disable colors per handler