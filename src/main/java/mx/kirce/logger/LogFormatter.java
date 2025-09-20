/*
 * Copyright 2025 Mix (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Improved utility class for formatting log messages in KirCE Logger.
 *
 * <p>This formatter supports custom templates for the log output. Users can define
 * the order of timestamp, level, tag, and message using placeholders:</p>
 *
 * <ul>
 *     <li>{time} - the formatted timestamp</li>
 *     <li>{level} - the log level</li>
 *     <li>{tag} - the log tag</li>
 *     <li>{message} - the log message</li>
 * </ul>
 *
 * <p>Supports optional colored output using ANSI codes. Use {@link #setUseColor(boolean)}
 * to enable or disable colors.</p>
 *
 * <pre>
 * LogFormatter formatter = new LogFormatter("HH:mm:ss", "{level} - {tag} -> {message}");
 * formatter.setUseColor(true);
 * String log = formatter.format(LogLevel.INFO, "Main", "Application started!");
 * </pre>
 */
public class LogFormatter {
    private final DateTimeFormatter timeFormatter;
    private final String template;
    private boolean useColor = false;

    /**
     * Creates a new LogFormatter with a date-time pattern and custom template.
     *
     * @param timePattern the date-time pattern (e.g., "yyyy-MM-dd HH:mm:ss")
     * @param template    the template for log messages using {time}, {level}, {tag}, {message}
     */
    public LogFormatter(String timePattern, String template) {
        this.timeFormatter = DateTimeFormatter.ofPattern(timePattern);
        this.template = template != null ? template : "[{time}] [{level}] {tag}: {message}";
    }

    /**
     * Enables or disables colored output.
     *
     * @param useColor true to enable colors, false for plain text
     */
    public void setUseColor(boolean useColor) {
        this.useColor = useColor;
    }

    /**
     * Formats a log message according to the template.
     *
     * @param level   the log level
     * @param tag     the source tag
     * @param message the log message
     * @return formatted log string
     */
    public String format(LogLevel level, String tag, String message) {
        String time = LocalDateTime.now().format(timeFormatter);
        String levelStr = level.toString();

        if (useColor) {
            levelStr = level.getColorCode() + levelStr + "\u001B[0m"; // reset color
        }

        return template
                .replace("{time}", time)
                .replace("{level}", levelStr)
                .replace("{tag}", tag)
                .replace("{message}", message);
    }
}