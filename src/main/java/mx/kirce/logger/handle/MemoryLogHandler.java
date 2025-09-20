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
import mx.kirce.logger.LogFormatter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * MemoryLogHandler stores the last N log messages in memory.
 *
 * <p>This handler allows keeping log messages for later analysis, debugging UI display,
 * or sending to remote servers. Supports optional log formatting, color codes, and filtering
 * by minimum log level.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * LogFormatter formatter = new LogFormatter("HH:mm:ss", "{level} | {tag}: {message}");
 * MemoryLogHandler memoryHandler = new MemoryLogHandler(100, formatter, true);
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(memoryHandler);
 *
 * logger.info("Example log message");
 * List&lt;String&gt; logs = memoryHandler.getLogs();
 * </pre>
 */
public class MemoryLogHandler implements LogHandler {

    /** Maximum number of messages to store in memory */
    private final int maxSize;

    /** Optional formatter for log messages */
    private final LogFormatter formatter;

    /** Thread-safe deque to store log messages */
    private final Deque<String> logMemory = new ArrayDeque<>();

    /** Minimum log level to store */
    private LogLevel minLevel = LogLevel.TRACE;

    /** Use ANSI color codes in stored messages */
    private final boolean useColors;

    /**
     * Creates a MemoryLogHandler with a specified maximum number of messages.
     * Uses default formatter and stores all levels.
     *
     * @param maxSize Maximum number of messages to keep in memory (minimum 1)
     */
    public MemoryLogHandler(int maxSize) {
        this(maxSize, null, false);
    }

    /**
     * Creates a MemoryLogHandler with a specified maximum number of messages,
     * custom formatter, and optional colors.
     *
     * @param maxSize   Maximum number of messages to keep in memory (minimum 1)
     * @param formatter Custom log formatter (if null, default is used)
     * @param useColors Whether to store ANSI color codes with the log messages
     */
    public MemoryLogHandler(int maxSize, LogFormatter formatter, boolean useColors) {
        this.maxSize = Math.max(1, maxSize);
        this.formatter = formatter != null ? formatter : new LogFormatter("yyyy-MM-dd HH:mm:ss", "[{time}] [{level}] {tag}: {message}");
        this.useColors = useColors;
    }

    /**
     * Sets the minimum log level to store.
     * Messages below this level will be ignored.
     *
     * @param level Minimum log level to store
     */
    public void setMinLevel(LogLevel level) {
        if (level != null) minLevel = level;
    }

    /**
     * Processes a log message and stores it in memory.
     *
     * @param level   The log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
     * @param tag     The tag identifying the log source (e.g., class name)
     * @param message The log message text
     */
    @Override
    public synchronized void log(LogLevel level, String tag, String message) {
        if (level.ordinal() < minLevel.ordinal() || message == null) return;

        String formatted = formatter.format(level, tag, message);

        if (useColors) {
            formatted = level.getColorCode() + formatted + "\u001B[0m";
        }

        logMemory.addLast(formatted);

        // Remove oldest message if limit exceeded
        if (logMemory.size() > maxSize) {
            logMemory.removeFirst();
        }
    }

    /**
     * Returns a copy of all messages currently stored in memory.
     *
     * @return List of log messages
     */
    public synchronized List<String> getLogs() {
        return new ArrayList<>(logMemory);
    }

    /**
     * Clears all log messages from memory.
     */
    public synchronized void clear() {
        logMemory.clear();
    }
}