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

/**
 * A [LogHandler] implementation that writes log messages
 * to the system console with optional color support.
 *
 * This handler can be used for debugging in console applications.
 *
 * Colors are derived from [LogLevel.colorCode] if enabled.
 *
 * Example usage:
 * ```
 * val logger = KirCELogger("Main")
 * logger.addHandler(ConsoleLogHandler(true)) // enable colors
 * logger.info("Hello from KirCE Logger!")
 *
 * // Disable colors
 * logger.addHandler(ConsoleLogHandler(false))
 * ```
 */
class ConsoleLogHandler(private val useColors: Boolean = true) : LogHandler {

    /**
     * Logs the given message to the console, applying colors if enabled.
     *
     * @param level   the log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
     * @param tag     the source tag for the log message
     * @param message the formatted log message
     */
    override fun log(level: LogLevel, tag: String, message: String) {
        val output = if (useColors) "${level.colorCode}$message\u001B[0m" else message
        println(output)
    }
}