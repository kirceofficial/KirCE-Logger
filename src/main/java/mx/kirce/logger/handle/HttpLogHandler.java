/*
 * Copyright 2025 Mix (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.handle;

import mx.kirce.logger.LogLevel;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * LogHandler implementation that sends logs to a remote HTTP endpoint.
 * <p>
 * Supports asynchronous sending, queueing, and automatic retry on failure.
 * Ensures that logging does not block the main application.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * KirCELogger logger = new KirCELogger("Main");
 * logger.addHandler(new HttpLogHandler(
 *     "https://example.com/log",
 *     3,        // retries
 *     5000,     // connection timeout in ms
 *     5000      // read timeout in ms
 * ));
 * logger.info("HTTP log sent!");
 * </pre>
 */
public class HttpLogHandler implements LogHandler {

    private final String endpointUrl;
    private final int maxRetries;
    private final int connectTimeout;
    private final int readTimeout;
    private final BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor;

    /**
     * Represents a single log entry.
     */
    private static class LogEntry {
        final LogLevel level;
        final String tag;
        final String message;
        final int retryCount;

        LogEntry(LogLevel level, String tag, String message, int retryCount) {
            this.level = level;
            this.tag = tag;
            this.message = message;
            this.retryCount = retryCount;
        }
    }

    /**
     * Creates an HttpLogHandler with default timeouts (5000 ms) and 3 retries.
     *
     * @param endpointUrl the URL to which logs will be sent
     */
    public HttpLogHandler(String endpointUrl) {
        this(endpointUrl, 3, 5000, 5000);
    }

    /**
     * Creates an HttpLogHandler with custom retries and timeouts.
     *
     * @param endpointUrl   the URL to which logs will be sent
     * @param maxRetries    maximum number of retries on failure
     * @param connectTimeout connection timeout in milliseconds
     * @param readTimeout    read timeout in milliseconds
     */
    public HttpLogHandler(String endpointUrl, int maxRetries, int connectTimeout, int readTimeout) {
        this.endpointUrl = endpointUrl;
        this.maxRetries = maxRetries;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.executor = Executors.newSingleThreadExecutor();
        startQueueProcessor();
    }

    /**
     * Adds a log entry to the queue and triggers asynchronous sending.
     *
     * @param level   the log level
     * @param tag     the log tag
     * @param message the log message
     */
    @Override
    public void log(LogLevel level, String tag, String message) {
        logQueue.offer(new LogEntry(level, tag, message, 0));
    }

    /**
     * Starts a background thread to process the log queue.
     */
    private void startQueueProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    LogEntry entry = logQueue.take();
                    sendLog(entry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Sends a single log entry via HTTP POST. Retries on failure.
     *
     * @param entry the log entry to send
     */
    private void sendLog(LogEntry entry) {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = String.format(
                    "{\"level\":\"%s\",\"tag\":\"%s\",\"message\":\"%s\"}",
                    entry.level, entry.tag, entry.message.replace("\"", "\\\"")
            );

            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            connection.getResponseCode(); // trigger send
            connection.disconnect();
        } catch (Exception e) {
            if (entry.retryCount < maxRetries) {
                logQueue.offer(new LogEntry(entry.level, entry.tag, entry.message, entry.retryCount + 1));
            } else {
                // Optionally: print to console if failed after max retries
                System.err.println("HttpLogHandler failed to send log after retries: " + entry.message);
            }
        }
    }

    /**
     * Shuts down the executor and stops sending logs.
     * Call this when application exits to clean up resources.
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}