/*
 * Copyright 2026 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.handle

import mx.kirce.logger.LogHandler
import mx.kirce.logger.LogLevel
import mx.kirce.logger.LogFormatter
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList

/**
 * MemoryLogHandler stores log messages in memory with advanced features:
 * - Thread-safe storage
 * - Level filtering (multiple levels)
 * - Tag filtering
 * - Callbacks for new log messages
 * - Statistics and metrics
 *
 * Example usage:
 * ```
 * val formatter = LogFormatter("HH:mm:ss", "{level} | {tag}: {message}")
 * val memoryHandler = MemoryLogHandler(200, formatter, true)
 * memoryHandler.setEnabledLevels(LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR)
 * memoryHandler.setEnabledTags("Main", "Network")
 * memoryHandler.addCallback(::println)
 *
 * val logger = KirCELogger("Main")
 * logger.addHandler(memoryHandler)
 * logger.info("This is an info log")
 * ```
 */
class MemoryLogHandler(
    val maxSize: Int,
    val formatter: LogFormatter = LogFormatter(
        "yyyy-MM-dd HH:mm:ss",
        "[{time}] [{level}] {tag}: {message}"
    ),
    private val useColors: Boolean = false
) : LogHandler {

    private val logMemory: Deque<String> = ConcurrentLinkedDeque()
    private val levelCount: MutableMap<LogLevel, Int> = EnumMap(LogLevel::class.java).apply {
        LogLevel.values().forEach { put(it, 0) }
    }
    private val enabledLevels: MutableSet<LogLevel> = EnumSet.allOf(LogLevel::class.java)
    private val enabledTags: MutableSet<String> = HashSet()
    private val callbacks: MutableList<(String) -> Unit> = CopyOnWriteArrayList()

    /**
     * Sets which log levels are stored by this handler.
     * If not set, all levels are stored.
     *
     * @param levels levels to store
     */
    fun setEnabledLevels(vararg levels: LogLevel) {
        enabledLevels.clear()
        if (levels.isNotEmpty()) {
            enabledLevels.addAll(levels)
        } else {
            enabledLevels.addAll(EnumSet.allOf(LogLevel::class.java))
        }
    }

    /**
     * Sets which tags are stored by this handler.
     * Empty means all tags are allowed.
     *
     * @param tags tags to allow
     */
    fun setEnabledTags(vararg tags: String) {
        enabledTags.clear()
        enabledTags.addAll(tags)
    }

    /**
     * Adds a callback to be executed when a new message is logged.
     *
     * @param callback callback function
     */
    fun addCallback(callback: (String) -> Unit) {
        callbacks.add(callback)
    }

    /**
     * Removes a callback.
     *
     * @param callback callback function
     */
    fun removeCallback(callback: (String) -> Unit) {
        callbacks.remove(callback)
    }

    /**
     * Processes a log message and stores it in memory if it matches filters.
     */
    override fun log(level: LogLevel, tag: String, message: String) {
        if (!enabledLevels.contains(level)) return
        if (enabledTags.isNotEmpty() && !enabledTags.contains(tag)) return

        var formatted = formatter.format(level, tag, message)
        if (useColors) formatted = "${level.colorCode}$formatted\u001B[0m"

        logMemory.addLast(formatted)
        levelCount[level] = (levelCount[level] ?: 0) + 1

        while (logMemory.size > maxSize) logMemory.pollFirst()

        callbacks.forEach { it(formatted) }
    }

    /** Returns all stored messages. */
    fun getLogs(): List<String> = ArrayList(logMemory)

    /** Returns all stored messages in reverse order. */
    fun getLogsReversed(): List<String> = ArrayList(logMemory).asReversed()

    /** Returns messages filtered by log level. */
    fun getLogsByLevel(level: LogLevel): List<String> =
        logMemory.filter { it.contains("[${level.name}]") }

    /** Returns messages filtered by tag. */
    fun getLogsByTag(tag: String): List<String> =
        logMemory.filter { it.contains("[$tag]") }

    /** Returns statistics: number of messages per log level. */
    fun getLevelCounts(): Map<LogLevel, Int> = EnumMap(levelCount)

    /** Returns current number of stored messages. */
    fun getSize(): Int = logMemory.size

    /** Clears all stored messages and resets statistics. */
    fun clear() {
        logMemory.clear()
        LogLevel.values().forEach { levelCount[it] = 0 }
    }
}