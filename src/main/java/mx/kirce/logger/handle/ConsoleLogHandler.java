/*
 * Copyright 2025 Mix (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.handle;

import mx.kirce.logger.LogLevel;

/**
 * A {@link LogHandler} implementation that writes log messages
 * to the system console with optional color support.
 *
 * <p>This handler can be used for debugging in console applications or Android Studio Logcat.</p>
 *
 * <p>Colors are derived from {@link LogLevel#getColorCode()} if enabled.</p>
 *
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new ConsoleLogHandler(true)); // enable colors
 * logger.info("Hello from KirCE Logger!");
 *
 * // Disable colors
 * logger.addHandler(new ConsoleLogHandler(false));
 * </pre>
 */
public class ConsoleLogHandler implements LogHandler {

    private final boolean useColors;

    /**
     * Creates a ConsoleLogHandler with colors enabled by default.
     */
    public ConsoleLogHandler() {
        this(true);
    }

    /**
     * Creates a ConsoleLogHandler with configurable color output.
     *
     * @param useColors if true, log messages will include ANSI colors
     */
    public ConsoleLogHandler(boolean useColors) {
        this.useColors = useColors;
    }

    /**
     * Logs the given message to the console, applying colors if enabled.
     *
     * @param level   the log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
     * @param tag     the source tag for the log message
     * @param message the formatted log message
     */
    @Override
    public void log(LogLevel level, String tag, String message) {
        String output = message;
        if (useColors) {
            output = level.getColorCode() + message + "\u001B[0m";
        }
        System.out.println(output);
    }
}