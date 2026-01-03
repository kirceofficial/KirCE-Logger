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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Improved utility class for formatting log messages in KirCE Logger.
 *
 * Supports custom templates with placeholders:
 * {time} - formatted timestamp
 * {level} - log level
 * {tag} - log tag
 * {message} - log message
 *
 * Supports optional colored output using ANSI codes.
 */
class LogFormatter(
    timePattern: String,
    private val template: String? = "[{time}] [{level}] {tag}: {message}"
) {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timePattern)
    var useColor: Boolean = false

    fun format(level: LogLevel, tag: String, message: String): String {
        val time = LocalDateTime.now().format(timeFormatter)
        var levelStr = level.name
        if (useColor) {
            levelStr = "${level.colorCode}$levelStr\u001B[0m"
        }
        return template!!
            .replace("{time}", time)
            .replace("{level}", levelStr)
            .replace("{tag}", tag)
            .replace("{message}", message)
    }
}