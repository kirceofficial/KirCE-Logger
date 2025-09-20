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

/**
 * Enum representing different log levels for KirCE Logger.
 * 
 * <p>Log levels determine the severity and type of log messages.
 * They can be used to filter and categorize logs in your application.
 * Each level has an associated ANSI color code for colored console output.</p>
 */
public enum LogLevel {

    /** Fine-grained trace messages for debugging. Color: Blue */
    TRACE("\u001B[34m"),

    /** General debugging information. Color: Cyan */
    DEBUG("\u001B[36m"),

    /** Informational messages to indicate normal operation. Color: Green */
    INFO("\u001B[32m"),

    /** Warnings for potentially problematic situations. Color: Yellow */
    WARN("\u001B[33m"),

    /** Errors that do not stop the application. Color: Red */
    ERROR("\u001B[31m"),

    /** Severe errors likely leading to application termination. Color: Magenta */
    FATAL("\u001B[35m");

    private final String colorCode;

    /**
     * Constructs a LogLevel with its associated ANSI color code.
     *
     * @param colorCode the ANSI escape code representing the color
     */
    LogLevel(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * Returns the ANSI color code associated with this log level.
     * Useful for colored console output.
     *
     * @return the ANSI escape code for the color
     */
    public String getColorCode() {
        return colorCode;
    }
}