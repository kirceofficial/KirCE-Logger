/*
 * Copyright 2025 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger;

import mx.kirce.logger.handle.LogHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Core class of the KirCE Logger framework.
 *
 * <p>This logger provides flexible logging with support for multiple handlers,
 * customizable formatters, adjustable minimum log levels, optional colored output,
 * asynchronous logging, and contextual metadata. Handlers can be attached per-instance
 * or globally for all loggers.</p>
 *
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new ConsoleLogHandler());
 * logger.info("Application started!");
 * </pre>
 */
public class KirCELogger {

    private final String tag;
    private final List<LogHandler> handlers = new CopyOnWriteArrayList<>();
    private final LogFormatter formatter;
    private LogLevel minLevel = LogLevel.TRACE;
    private boolean useColors = false;
    private final ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();

    private static final List<LogHandler> globalHandlers = new CopyOnWriteArrayList<>();
    private static volatile LogLevel globalMinLevel = LogLevel.TRACE;
    private static volatile boolean globalUseColors = false;
    private static volatile LogFormatter globalDefaultFormatter =
            new LogFormatter("yyyy-MM-dd HH:mm:ss", "[{time}] [{level}] {tag}: {message}");
    private static ExecutorService asyncExecutor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("KirCELogger-Async-" + t.getId());
                return t;
            });

    /**
     * Creates a logger with default global formatter and colors.
     *
     * @param tag the tag identifying the source of the log (e.g., class name)
     */
    public KirCELogger(String tag) {
        this(tag, globalDefaultFormatter);
        this.useColors = globalUseColors;
    }

    /**
     * Creates a logger with a custom formatter.
     *
     * @param tag       the tag identifying the source of the log (e.g., class name)
     * @param formatter the formatter used for log messages
     */
    public KirCELogger(String tag, LogFormatter formatter) {
        this.tag = tag;
        this.formatter = formatter != null ? formatter : globalDefaultFormatter;
        this.useColors = globalUseColors;
    }

    /**
     * Enables or disables colored output for this logger.
     *
     * @param enable true to enable colors, false to disable
     */
    public void enableColors(boolean enable) {
        this.useColors = enable;
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
     * Adds a global handler applied to all logger instances.
     *
     * @param handler the global handler to add
     */
    public static void addGlobalHandler(LogHandler handler) {
        if (handler != null) globalHandlers.add(handler);
    }

    /**
     * Sets the minimum log level for this logger.
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
     * Sets global colored output for all loggers.
     *
     * @param enable true to enable colors globally
     */
    public static void setGlobalUseColors(boolean enable) {
        globalUseColors = enable;
    }

    /**
     * Sets global default formatter for all loggers.
     *
     * @param formatter the formatter
     */
    public static void setGlobalDefaultFormatter(LogFormatter formatter) {
        if (formatter != null) globalDefaultFormatter = formatter;
    }

    /**
     * Sets custom ExecutorService for asynchronous logging.
     *
     * @param executor ExecutorService instance
     */
    public static void setAsyncExecutor(ExecutorService executor) {
        if (executor != null) asyncExecutor = executor;
    }

    /**
     * Gracefully shuts down the asynchronous logger executor.
     */
    public static void shutdownAsyncExecutor() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Adds or updates a context value for this logger.
     *
     * @param key   context key
     * @param value context value
     */
    public void putContext(String key, Object value) {
        if (key != null && value != null) context.put(key, value);
    }

    /**
     * Removes a context value.
     *
     * @param key context key
     */
    public void removeContext(String key) {
        if (key != null) context.remove(key);
    }

    /**
     * Returns an unmodifiable map of current context values.
     */
    public Map<String, Object> getContext() {
        return Map.copyOf(context);
    }

    public void log(LogLevel level, String message) {
    if (message == null || level.ordinal() < minLevel.ordinal() || level.ordinal() < globalMinLevel.ordinal())
        return;

    String formatted = formatter.format(level, tag, message);
    if (useColors) formatted = level.getColorCode() + formatted + "\u001B[0m";

    final String finalTag = tag;
    final String finalFormatted = formatted;
    final LogLevel finalLevel = level;

    handlers.forEach(h -> h.log(finalLevel, finalTag, finalFormatted));
    synchronized (globalHandlers) {
        globalHandlers.forEach(h -> h.log(finalLevel, finalTag, finalFormatted));
    }
}

    /**
     * Logs asynchronously with default executor.
     */
    public void logAsync(LogLevel level, String message) {
        asyncExecutor.submit(() -> log(level, message));
    }

    public void trace(String message) { log(LogLevel.TRACE, message); }
    public void debug(String message) { log(LogLevel.DEBUG, message); }
    public void info(String message)  { log(LogLevel.INFO, message); }
    public void warn(String message)  { log(LogLevel.WARN, message); }
    public void error(String message) { log(LogLevel.ERROR, message); }
    public void fatal(String message) { log(LogLevel.FATAL, message); }
    public void verbose(String message) {
log(LogLevel.VERBOSE, message); }

    public void traceAsync(String message) { logAsync(LogLevel.TRACE, message); }
    public void debugAsync(String message) { logAsync(LogLevel.DEBUG, message); }
    public void infoAsync(String message)  { logAsync(LogLevel.INFO, message); }
    public void warnAsync(String message)  { logAsync(LogLevel.WARN, message); }
    public void errorAsync(String message) { logAsync(LogLevel.ERROR, message); }
    public void fatalAsync(String message) { logAsync(LogLevel.FATAL, message); }
    public void verboseAsync(String
message) { logAsync(LogLevel.VERBOSE, message); }

    /**
     * Quickly get a logger instance with default global formatter and settings.
     *
     * @param tag The tag identifying the source
     * @return KirCELogger instance
     */
    public static KirCELogger getLogger(String tag) {
        return new KirCELogger(tag);
    }
}