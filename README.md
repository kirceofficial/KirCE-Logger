# KirCE Logger

[![](https://jitpack.io/v/kirceofficial/KirCE-Logger.svg)](https://jitpack.io/#kirceofficial/KirCE-Logger)

![KirCE Logger Logo](images/logo.png)

**KirCE Logger** is an open-source, high-performance Java logger library.  
It is **cross-platform**, working seamlessly on **Android**, **Desktop**, and **Server** applications.  
Designed for developers who need **flexible, extensible, and color-coded logging**.

---

## Features

- Cross-platform: Android, Desktop, and Server
- Supports multiple log levels:
  - `TRACE` – Fine-grained debug information
  - `DEBUG` – General debug info
  - `INFO` – Informational messages
  - `WARN` – Warnings for potential issues
  - `ERROR` – Recoverable errors
  - `FATAL` – Severe errors likely causing termination
  - `VERBOSE` - White/Gray	Extremely detailed messages, intended for in-depth debugging and tracing internal application behavior
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
    implementation 'com.github.kirceofficial:KirCE-Logger:+'
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
        logger.verbose("VERBOSE message")
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
