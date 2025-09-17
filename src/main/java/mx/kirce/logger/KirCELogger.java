/*
 * Copyright 2025 Mix (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.kirce.logger;

import mx.kirce.logger.handle.LogHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Core class of the KirCE Logger framework.
 *
 * <p>This logger provides flexible logging with support for multiple handlers,
 * customizable formatters, and adjustable minimum log levels. Handlers can be
 * attached either per-instance or globally for all loggers.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new ConsoleLogHandler());
 * logger.info("Application started!");
 * </pre>
 */
public class KirCELogger {
    private final String tag;
    private final List<LogHandler> handlers = new ArrayList<>();
    private final LogFormatter formatter;
    private LogLevel minLevel = LogLevel.TRACE;

    private static final List<LogHandler> globalHandlers = new ArrayList<>();

    /**
     * Creates a logger with a default formatter (yyyy-MM-dd HH:mm:ss).
     *
     * @param tag the tag identifying the source of the log (e.g., class name)
     */
    public KirCELogger(String tag) {
        this.tag = tag;
        this.formatter = new LogFormatter("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Creates a logger with a custom formatter.
     *
     * @param tag       the tag identifying the source of the log (e.g., class name)
     * @param formatter the formatter used for log messages
     */
    public KirCELogger(String tag, LogFormatter formatter) {
        this.tag = tag;
        this.formatter = formatter;
    }

    /**
     * Adds a handler specific to this logger instance.
     *
     * @param handler the handler to add
     */
    public void addHandler(LogHandler handler) {
        handlers.add(handler);
    }

    /**
     * Adds a handler that applies to all logger instances globally.
     *
     * @param handler the global handler to add
     */
    public static void addGlobalHandler(LogHandler handler) {
        globalHandlers.add(handler);
    }

    /**
     * Sets the minimum log level for this logger.
     * Messages with a lower level will be ignored.
     *
     * @param level the minimum log level
     */
    public void setMinLevel(LogLevel level) {
        this.minLevel = level;
    }

    /**
     * Internal method to process and deliver log messages.
     *
     * @param level   the log level
     * @param message the message text
     */
    private void log(LogLevel level, String message) {
        if (level.ordinal() < minLevel.ordinal()) return;

        String formatted = formatter.format(level, tag, message);

        for (LogHandler handler : handlers) {
            handler.log(level, tag, formatted);
        }
        for (LogHandler handler : globalHandlers) {
            handler.log(level, tag, formatted);
        }
    }

    /** Logs a TRACE level message. */
    public void trace(String message) { log(LogLevel.TRACE, message); }

    /** Logs a DEBUG level message. */
    public void debug(String message) { log(LogLevel.DEBUG, message); }

    /** Logs an INFO level message. */
    public void info(String message)  { log(LogLevel.INFO, message); }

    /** Logs a WARN level message. */
    public void warn(String message)  { log(LogLevel.WARN, message); }

    /** Logs an ERROR level message. */
    public void error(String message) { log(LogLevel.ERROR, message); }

    /** Logs a FATAL level message. */
    public void fatal(String message) { log(LogLevel.FATAL, message); }
}