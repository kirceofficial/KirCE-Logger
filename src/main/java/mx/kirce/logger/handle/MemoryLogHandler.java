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

import mx.kirce.logger.handle.LogHandler;
import mx.kirce.logger.LogLevel;

import java.util.LinkedList;
import java.util.List;

/**
 * MemoryLogHandler stores the last N log messages in memory.
 *
 * <p>This handler allows keeping log messages in memory for later analysis
 * or debugging UI display.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * MemoryLogHandler memoryHandler = new MemoryLogHandler(100); // keep last 100 messages
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(memoryHandler);
 *
 * logger.info("Example log message");
 * List&lt;String&gt; logs = memoryHandler.getLogs();
 * </pre>
 */
public class MemoryLogHandler implements LogHandler {

    /** Maximum number of messages to store in memory */
    private final int maxSize;

    /** Linked list to store log messages */
    private final LinkedList<String> logMemory = new LinkedList<>();

    /**
     * Creates a MemoryLogHandler with a specified maximum number of stored messages.
     *
     * @param maxSize Maximum number of messages to keep in memory (minimum 1)
     */
    public MemoryLogHandler(int maxSize) {
        this.maxSize = Math.max(1, maxSize);
    }

    /**
     * Processes a log message and stores it in memory.
     *
     * @param level   The log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
     * @param tag     The tag identifying the log source (e.g., class name)
     * @param message The log message text
     */
    @Override
    public synchronized void log(LogLevel level, String tag, String message) {
        String entry = "[" + level + "] " + tag + ": " + message;
        logMemory.add(entry);

        // Remove the oldest message if the size limit is exceeded
        if (logMemory.size() > maxSize) {
            logMemory.removeFirst();
        }
    }

    /**
     * Returns a list of all messages currently stored in memory.
     *
     * @return A copy of the list of log messages
     */
    public synchronized List<String> getLogs() {
        return new LinkedList<>(logMemory);
    }

    /**
     * Clears all log messages from memory.
     */
    public synchronized void clear() {
        logMemory.clear();
    }
}