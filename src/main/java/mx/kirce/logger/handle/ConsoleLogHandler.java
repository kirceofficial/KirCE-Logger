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
 * A simple {@link LogHandler} implementation that writes log messages
 * to the system console using {@link System#out}, with optional color support.
 *
 * <p>This handler is useful for debugging and development purposes,
 * as it outputs logs directly to standard output.</p>
 *
 * <p>Colors can be enabled or disabled using the constructor.</p>
 *
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new ConsoleLogHandler(true)); // with colors
 * logger.info("Hello from KirCE Logger!");
 * </pre>
 */
public class ConsoleLogHandler implements LogHandler {

    private final boolean useColors;

    // ANSI escape codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";

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
     * Logs the given message to the system console, applying color if enabled.
     *
     * @param level   the log level (e.g., INFO, WARN, ERROR)
     * @param tag     the tag identifying the source of the log
     * @param message the formatted log message
     */
    @Override
    public void log(LogLevel level, String tag, String message) {
        String output = message;

        if (useColors) {
            switch (level) {
                case TRACE -> output = CYAN + message + RESET;
                case DEBUG -> output = message; // default console color
                case INFO -> output = GREEN + message + RESET;
                case WARN -> output = YELLOW + message + RESET;
                case ERROR, FATAL -> output = RED + message + RESET;
            }
        }

        System.out.println(output);
    }
}