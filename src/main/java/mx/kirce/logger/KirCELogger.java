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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Core class of the KirCE Logger framework.
 *
 * <p>This logger provides flexible logging with support for multiple handlers,
 * customizable formatters, adjustable minimum log levels, and asynchronous logging.
 * Handlers can be attached either per-instance or globally for all loggers.</p>
 *
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

    private static final List<LogHandler> globalHandlers = Collections.synchronizedList(new ArrayList<>());
    private static LogLevel globalMinLevel = LogLevel.TRACE;

    private static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    /**
     * Creates a logger with a default formatter (yyyy-MM-dd HH:mm:ss).
     *
     * @param tag the tag identifying the source of the log (e.g., class name)
     */
    public KirCELogger(String tag) {
        this(tag, new LogFormatter("yyyy-MM-dd HH:mm:ss"));
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
        if (handler != null) handlers.add(handler);
    }

    /**
     * Adds a handler that applies to all logger instances globally.
     *
     * @param handler the global handler to add
     */
    public static void addGlobalHandler(LogHandler handler) {
        if (handler != null) globalHandlers.add(handler);
    }

    /**
     * Sets the minimum log level for this logger.
     * Messages with a lower level will be ignored.
     *
     * @param level the minimum log level
     */
    public void setMinLevel(LogLevel level) {
        if (level != null) this.minLevel = level;
    }

    /**
     * Sets the global minimum log level for all loggers.
     *
     * @param level the global minimum log level
     */
    public static void setGlobalMinLevel(LogLevel level) {
        if (level != null) globalMinLevel = level;
    }

    /**
     * Internal method to process and deliver log messages synchronously.
     *
     * @param level   the log level
     * @param message the message text
     */
    private void log(LogLevel level, String message) {
        if (level.ordinal() < minLevel.ordinal() || level.ordinal() < globalMinLevel.ordinal() || message == null)
            return;

        String formatted = formatter.format(level, tag, message);

        handlers.forEach(h -> h.log(level, tag, formatted));
        synchronized (globalHandlers) {
            globalHandlers.forEach(h -> h.log(level, tag, formatted));
        }
    }

    /**
     * Internal method to process and deliver log messages asynchronously.
     *
     * @param level   the log level
     * @param message the message text
     */
    public void logAsync(LogLevel level, String message) {
        asyncExecutor.submit(() -> log(level, message));
    }

    /** Logs a TRACE level message.
     * @param message the message to log
     */
    public void trace(String message) { log(LogLevel.TRACE, message); }

    /** Logs a DEBUG level message.
     * @param message the message to log
     */
    public void debug(String message) { log(LogLevel.DEBUG, message); }

    /** Logs an INFO level message.
     * @param message the message to log
     */
    public void info(String message)  { log(LogLevel.INFO, message); }

    /** Logs a WARN level message.
     * @param message the message to log
     */
    public void warn(String message)  { log(LogLevel.WARN, message); }

    /** Logs an ERROR level message.
     * @param message the message to log
     */
    public void error(String message) { log(LogLevel.ERROR, message); }

    /** Logs a FATAL level message.
     * @param message the message to log
     */
    public void fatal(String message) { log(LogLevel.FATAL, message); }

    /** Asynchronous TRACE level logging.
     * @param message the message to log
     */
    public void traceAsync(String message) { logAsync(LogLevel.TRACE, message); }

    /** Asynchronous DEBUG level logging.
     * @param message the message to log
     */
    public void debugAsync(String message) { logAsync(LogLevel.DEBUG, message); }

    /** Asynchronous INFO level logging.
     * @param message the message to log
     */
    public void infoAsync(String message)  { logAsync(LogLevel.INFO, message); }

    /** Asynchronous WARN level logging.
     * @param message the message to log
     */
    public void warnAsync(String message)  { logAsync(LogLevel.WARN, message); }

    /** Asynchronous ERROR level logging.
     * @param message the message to log
     */
    public void errorAsync(String message) { logAsync(LogLevel.ERROR, message); }

    /** Asynchronous FATAL level logging.
     * @param message the message to log
     */
    public void fatalAsync(String message) { logAsync(LogLevel.FATAL, message); }

    /**
     * Quickly get a logger instance with default formatter.
     *
     * @param tag The tag identifying the source
     * @return KirCELogger instance
     */
    public static KirCELogger getLogger(String tag) {
        return new KirCELogger(tag);
    }
}