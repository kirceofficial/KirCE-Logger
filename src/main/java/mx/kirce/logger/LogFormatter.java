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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting log messages in KirCE Logger.
 *
 * <p>This class is responsible for constructing log message strings
 * with a given time pattern, log level, tag, and message.</p>
 *
 * <p>Example output:</p>
 * <pre>
 * [2025-09-17 10:15:00] [INFO] Main: Application started successfully
 * </pre>
 */
public class LogFormatter {
    private final DateTimeFormatter timeFormatter;

    /**
     * Creates a new LogFormatter with a custom date-time pattern.
     *
     * @param pattern The date-time pattern (e.g., "yyyy-MM-dd HH:mm:ss")
     */
    public LogFormatter(String pattern) {
        this.timeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Formats a log message with timestamp, log level, tag, and message.
     *
     * @param level   The log level (e.g., INFO, WARN, ERROR)
     * @param tag     The tag indicating the source of the log (e.g., class name)
     * @param message The log message
     * @return A formatted log string
     */
    public String format(LogLevel level, String tag, String message) {
        String time = LocalDateTime.now().format(timeFormatter);
        return "[" + time + "] [" + level + "] " + tag + ": " + message;
    }
}