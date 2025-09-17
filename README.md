# KirCE Logger



**KirCE Logger** is an advanced and improved Java Logger with enhanced performance.\
It is cross-platform and suitable for any project.

## Features

- Cross-platform (works on Android, Desktop, and server applications)
- Supports different log levels: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`
- Easy integration into any project
- Extensible via `LogHandler`
- High performance with minimal resource consumption

## Example Usage

```java
import mx.kirce.logger.KirCELogger;
import mx.kirce.logger.handle.ConsoleLogHandler;

public class Main {
    public static void main(String[] args) {
        KirCELogger logger = new KirCELogger("Main");

        logger.addHandler(new ConsoleLogHandler());

        logger.trace("This is a TRACE message");
        logger.debug("This is a DEBUG message");
        logger.info("This is an INFO message");
        logger.warn("This is a WARN message");
        logger.error("This is an ERROR message");
        logger.fatal("This is a FATAL message");
    }
}
```

## Installation

Using Gradle:

```gradle
dependencies {
    implementation 'mx.kirce.logger:kirce-logger:1.0.0'
}
```
