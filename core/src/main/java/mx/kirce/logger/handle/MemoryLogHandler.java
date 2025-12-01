/*
 * Copyright 2025 KirCE (KirCE Logger)
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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * MemoryLogHandler stores log messages in memory with advanced features:
 * <ul>
 *     <li>Thread-safe storage</li>
 *     <li>Level filtering (multiple levels)</li>
 *     <li>Tag filtering</li>
 *     <li>Callbacks for new log messages</li>
 *     <li>Statistics and metrics</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * LogFormatter formatter = new LogFormatter("HH:mm:ss", "{level} | {tag}: {message}");
 * MemoryLogHandler memoryHandler = new MemoryLogHandler(200, formatter, true);
 * memoryHandler.setEnabledLevels(LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR);
 * memoryHandler.setEnabledTags("Main", "Network");
 * memoryHandler.addCallback(System.out::println);
 *
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(memoryHandler);
 * logger.info("This is an info log");
 * </pre>
 */
public class MemoryLogHandler implements LogHandler {

    /** Maximum number of messages to store in memory */
    private final int maxSize;

    /** Optional formatter for log messages */
    private final LogFormatter formatter;

    /** Use ANSI color codes in stored messages */
    private final boolean useColors;

    /** Thread-safe deque to store log messages */
    private final Deque<String> logMemory = new ConcurrentLinkedDeque<>();

    /** Count of messages per log level */
    private final Map<LogLevel, Integer> levelCount = new EnumMap<>(LogLevel.class);

    /** Enabled log levels */
    private final Set<LogLevel> enabledLevels = EnumSet.allOf(LogLevel.class);

    /** Enabled tags; empty set means all tags are allowed */
    private final Set<String> enabledTags = new HashSet<>();

    /** Callbacks to execute on each new log message */
    private final List<Consumer<String>> callbacks = new CopyOnWriteArrayList<>();

    /**
     * Creates a MemoryLogHandler with a specified maximum number of messages.
     * Uses default formatter and stores all levels.
     *
     * @param maxSize maximum number of messages to store
     */
    public MemoryLogHandler(int maxSize) {
        this(maxSize, null, false);
    }

    /**
     * Creates a MemoryLogHandler with custom formatter and ANSI color option.
     *
     * @param maxSize   maximum number of messages to store
     * @param formatter custom log formatter (if null, default is used)
     * @param useColors whether to include ANSI color codes
     */
    public MemoryLogHandler(int maxSize, LogFormatter formatter, boolean useColors) {
        this.maxSize = Math.max(1, maxSize);
        this.formatter = formatter != null
                ? formatter
                : new LogFormatter("yyyy-MM-dd HH:mm:ss", "[{time}] [{level}] {tag}: {message}");
        this.useColors = useColors;

        for (LogLevel level : LogLevel.values()) {
            levelCount.put(level, 0);
        }
    }

    /**
     * Sets which log levels are stored by this handler.
     * If not set, all levels are stored.
     *
     * @param levels levels to store
     */
    public void setEnabledLevels(LogLevel... levels) {
        enabledLevels.clear();
        if (levels != null && levels.length > 0) {
            enabledLevels.addAll(Arrays.asList(levels));
        } else {
            enabledLevels.addAll(EnumSet.allOf(LogLevel.class));
        }
    }

    /**
     * Sets which tags are stored by this handler.
     * Empty means all tags are allowed.
     *
     * @param tags tags to allow
     */
    public void setEnabledTags(String... tags) {
        enabledTags.clear();
        if (tags != null && tags.length > 0) {
            enabledTags.addAll(Arrays.asList(tags));
        }
    }

    /**
     * Adds a callback to be executed when a new message is logged.
     *
     * @param callback callback consumer
     */
    public void addCallback(Consumer<String> callback) {
        if (callback != null) callbacks.add(callback);
    }

    /**
     * Removes a callback.
     *
     * @param callback callback consumer
     */
    public void removeCallback(Consumer<String> callback) {
        callbacks.remove(callback);
    }

    /**
     * Processes a log message and stores it in memory if it matches filters.
     *
     * @param level   the log level
     * @param tag     the tag identifying the source
     * @param message the log message
     */
    @Override
    public void log(LogLevel level, String tag, String message) {
        if (level == null || message == null) return;
        if (!enabledLevels.contains(level)) return;
        if (!enabledTags.isEmpty() && !enabledTags.contains(tag)) return;

        String formatted = formatter.format(level, tag, message);
        if (useColors) formatted = level.getColorCode() + formatted + "\u001B[0m";

        logMemory.addLast(formatted);
        levelCount.put(level, levelCount.getOrDefault(level, 0) + 1);

        while (logMemory.size() > maxSize) logMemory.pollFirst();

        for (Consumer<String> cb : callbacks) cb.accept(formatted);
    }

    /**
     * Returns all stored messages.
     *
     * @return list of messages
     */
    public List<String> getLogs() {
        return new ArrayList<>(logMemory);
    }

    /**
     * Returns all stored messages in reverse order.
     *
     * @return reversed list of messages
     */
    public List<String> getLogsReversed() {
        List<String> list = new ArrayList<>(logMemory);
        Collections.reverse(list);
        return list;
    }

    /**
     * Returns messages filtered by log level.
     *
     * @param level log level to filter
     * @return list of messages matching level
     */
    public List<String> getLogsByLevel(LogLevel level) {
        List<String> result = new ArrayList<>();
        for (String log : logMemory) if (log.contains("[" + level.name() + "]")) result.add(log);
        return result;
    }

    /**
     * Returns messages filtered by tag.
     *
     * @param tag tag to filter
     * @return list of messages matching tag
     */
    public List<String> getLogsByTag(String tag) {
        List<String> result = new ArrayList<>();
        for (String log : logMemory) if (log.contains("[" + tag + "]")) result.add(log);
        return result;
    }

    /**
     * Returns statistics: number of messages per log level.
     *
     * @return map of level -> count
     */
    public Map<LogLevel, Integer> getLevelCounts() {
        return new EnumMap<>(levelCount);
    }

    /**
     * Returns current number of stored messages.
     *
     * @return size of log memory
     */
    public int getSize() {
        return logMemory.size();
    }

    /**
     * Clears all stored messages and resets statistics.
     */
    public void clear() {
        logMemory.clear();
        for (LogLevel level : LogLevel.values()) levelCount.put(level, 0);
    }
}