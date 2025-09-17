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
 * A simple {@link LogHandler} implementation that writes log messages
 * to the system console using {@link System#out}.
 *
 * <p>This handler is useful for debugging and development purposes,
 * as it outputs logs directly to standard output.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new ConsoleLogHandler());
 * logger.info("Hello from KirCE Logger!");
 * </pre>
 */
public class ConsoleLogHandler implements LogHandler {

    /**
     * Logs the given message to the system console.
     *
     * @param level   the log level (e.g., INFO, WARN, ERROR)
     * @param tag     the tag identifying the source of the log
     * @param message the formatted log message
     */
    @Override
    public void log(LogLevel level, String tag, String message) {
        System.out.println(message);
    }
}