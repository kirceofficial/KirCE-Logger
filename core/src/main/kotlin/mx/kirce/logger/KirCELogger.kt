/*
 * Copyright 2026 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger

import mx.kirce.logger.handle.LogHandler
import java.util.concurrent.*

/**
 * Core class of the KirCE Logger framework.
 *
 * This logger provides flexible logging with support for multiple handlers,
 * customizable formatters, adjustable minimum log levels, optional colored output,
 * asynchronous logging, and contextual metadata. Handlers can be attached per-instance
 * or globally for all loggers.
 *
 * Example usage:
 * ```
 * val logger = KirCELogger("Main")
 * logger.addHandler(ConsoleLogHandler())
 * logger.info("Application started!")
 * ```
 */
class KirCELogger(
    private val tag: String,
    private val formatter: LogFormatter = globalDefaultFormatter
) {

    private val handlers: MutableList<LogHandler> = CopyOnWriteArrayList()
    private var minLevel: LogLevel = LogLevel.TRACE
    var useColors: Boolean = globalUseColors
    private val context: ConcurrentMap<String, Any> = ConcurrentHashMap()

    companion object {
        private val globalHandlers: MutableList<LogHandler> = CopyOnWriteArrayList()
        @Volatile
        private var globalMinLevel: LogLevel = LogLevel.TRACE
        @Volatile
        private var globalUseColors: Boolean = false
        @Volatile
        private var globalDefaultFormatter: LogFormatter =
            LogFormatter("yyyy-MM-dd HH:mm:ss", "[{time}] [{level}] {tag}: {message}")
        private var asyncExecutor: ExecutorService = Executors.newCachedThreadPool { r ->
            Thread(r).apply {
                isDaemon = true
                name = "KirCELogger-Async-$id"
            }
        }

        /**
         * Adds a global handler applied to all logger instances.
         *
         * @param handler the global handler to add
         */
        fun addGlobalHandler(handler: LogHandler?) {
            handler?.let { globalHandlers.add(it) }
        }

        /**
         * Sets the global minimum log level for all loggers.
         *
         * @param level the global minimum log level
         */
        fun setGlobalMinLevel(level: LogLevel?) {
            level?.let { globalMinLevel = it }
        }

        /**
         * Enables or disables global colored output for all loggers.
         *
         * @param enable true to enable colors globally
         */
        fun setGlobalUseColors(enable: Boolean) {
            globalUseColors = enable
        }

        /**
         * Sets global default formatter for all loggers.
         *
         * @param formatter the formatter
         */
        fun setGlobalDefaultFormatter(formatter: LogFormatter?) {
            formatter?.let { globalDefaultFormatter = it }
        }

        /**
         * Sets custom ExecutorService for asynchronous logging.
         *
         * @param executor ExecutorService instance
         */
        fun setAsyncExecutor(executor: ExecutorService?) {
            executor?.let { asyncExecutor = it }
        }

        /**
         * Gracefully shuts down the asynchronous logger executor.
         */
        fun shutdownAsyncExecutor() {
            asyncExecutor.shutdown()
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow()
                }
            } catch (e: InterruptedException) {
                asyncExecutor.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }

        /**
         * Quickly get a logger instance with default global formatter and settings.
         *
         * @param tag The tag identifying the source
         * @return KirCELogger instance
         */
        fun getLogger(tag: String) = KirCELogger(tag)
    }

    /**
     * Enables or disables colored output for this logger.
     *
     * @param enable true to enable colors, false to disable
     */
    fun enableColors(enable: Boolean) {
        useColors = enable
    }

    /**
     * Adds a handler specific to this logger instance.
     *
     * @param handler the handler to add
     */
    fun addHandler(handler: LogHandler?) {
        handler?.let { handlers.add(it) }
    }

    /**
     * Sets the minimum log level for this logger.
     *
     * @param level the minimum log level
     */
    fun setMinLevel(level: LogLevel?) {
        level?.let { minLevel = it }
    }

    /**
     * Adds or updates a context value for this logger.
     *
     * @param key   context key
     * @param value context value
     */
    fun putContext(key: String?, value: Any?) {
        if (key != null && value != null) context[key] = value
    }

    /**
     * Removes a context value.
     *
     * @param key context key
     */
    fun removeContext(key: String?) {
        key?.let { context.remove(it) }
    }

    /**
     * Returns an unmodifiable map of current context values.
     */
    fun getContext(): Map<String, Any> = context.toMap()

    /**
     * Logs a message at the specified level.
     *
     * @param level   the log level
     * @param message the log message
     */
    fun log(level: LogLevel, message: String?) {
        if (message == null || level.ordinal < minLevel.ordinal || level.ordinal < globalMinLevel.ordinal) return

        var formatted = formatter.format(level, tag, message)
        if (useColors) formatted = "${level.colorCode}$formatted\u001B[0m"

        handlers.forEach { it.log(level, tag, formatted) }
        synchronized(globalHandlers) {
            globalHandlers.forEach { it.log(level, tag, formatted) }
        }
    }

    /**
     * Logs asynchronously with default executor.
     *
     * @param level   the log level
     * @param message the log message
     */
    fun logAsync(level: LogLevel, message: String?) {
        asyncExecutor.submit { log(level, message) }
    }

    fun trace(message: String?) = log(LogLevel.TRACE, message)
    fun debug(message: String?) = log(LogLevel.DEBUG, message)
    fun info(message: String?) = log(LogLevel.INFO, message)
    fun warn(message: String?) = log(LogLevel.WARN, message)
    fun error(message: String?) = log(LogLevel.ERROR, message)
    fun fatal(message: String?) = log(LogLevel.FATAL, message)
    fun verbose(message: String?) = log(LogLevel.VERBOSE, message)

    fun traceAsync(message: String?) = logAsync(LogLevel.TRACE, message)
    fun debugAsync(message: String?) = logAsync(LogLevel.DEBUG, message)
    fun infoAsync(message: String?) = logAsync(LogLevel.INFO, message)
    fun warnAsync(message: String?) = logAsync(LogLevel.WARN, message)
    fun errorAsync(message: String?) = logAsync(LogLevel.ERROR, message)
    fun fatalAsync(message: String?) = logAsync(LogLevel.FATAL, message)
    fun verboseAsync(message: String?) = logAsync(LogLevel.VERBOSE, message)
}