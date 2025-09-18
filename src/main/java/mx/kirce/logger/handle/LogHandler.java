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

package mx.kirce.logger.handle;

import mx.kirce.logger.LogLevel;

/**
 * Defines a handler for processing log messages in KirCE Logger.
 *
 * <p>Implementations of this interface decide what to do with log messages,
 * such as printing them to the console, saving them to a file, or sending them
 * to a remote logging server.</p>
 *
 * <p>Example:</p>
 * <pre>
 * public class ConsoleLogHandler implements LogHandler {
 *     public void log(LogLevel level, String tag, String message) {
 *         System.out.println("[" + level + "] " + tag + ": " + message);
 *     }
 * }
 * </pre>
 */
public interface LogHandler {
    /**
     * Handles a log message with a given level, tag, and message text.
     *
     * @param level   the log level (e.g., INFO, WARN, ERROR)
     * @param tag     the tag identifying the source of the log (e.g., class name)
     * @param message the log message
     */
    void log(LogLevel level, String tag, String message);
}