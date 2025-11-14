/*
 * Copyright 2025 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.android;

import android.util.Log;
import mx.kirce.logger.KirCELogger;
import mx.kirce.logger.LogLevel;
import mx.kirce.logger.handle.LogHandler;
import java.util.Map;

/**
 * AndroidLogger integrates KirCE Logger with Android Logcat.
 *
 * <p>Log messages are sent to both KirCE Logger handlers and Android Studio Logcat.
 * Supports context, colors, and asynchronous logging.</p>
 *
 * <pre>
 * AndroidLogger logger = new AndroidLogger("Main");
 * logger.putContext("userId", 42);
 * logger.info("Hello with context!");
 * </pre>
 */
public class AndroidLogger {

    private final KirCELogger kirceLogger;
    private final String tag;

    /**
     * Creates an AndroidLogger with the specified tag.
     *
     * @param tag the tag used for Android Logcat
     */
    public AndroidLogger(String tag) {
        this.tag = tag;
        this.kirceLogger = new KirCELogger(tag);
    }

    /**
     * Enables or disables colored log output.
     *
     * @param enable true to enable colors
     */
    public void enableColors(boolean enable) {
        kirceLogger.enableColors(enable);
    }

    /**
     * Adds a handler specific to this logger instance.
     *
     * @param handler the handler to add
     */
    public void addHandler(LogHandler handler) {
        if (handler != null) kirceLogger.addHandler(handler);
    }

    /**
     * Adds or updates a context value.
     *
     * @param key   context key
     * @param value context value
     */
    public void putContext(String key, Object value) {
        kirceLogger.putContext(key, value);
    }

    /**
     * Removes a context value.
     *
     * @param key context key
     */
    public void removeContext(String key) {
        kirceLogger.removeContext(key);
    }

    /**
     * Returns an unmodifiable map of current context values.
     */
    public Map<String, Object> getContext() {
        return kirceLogger.getContext();
    }

    private void logToAndroid(LogLevel level, String message) {
        switch (level) {
            case TRACE -> Log.v(tag, message);
            case DEBUG -> Log.d(tag, message);
            case INFO  -> Log.i(tag, message);
            case WARN  -> Log.w(tag, message);
            case ERROR -> Log.e(tag, message);
            case FATAL -> Log.wtf(tag, message);
        }
    }

    private void log(LogLevel level, String message) {
        kirceLogger.logAsync(level, message);
        logToAndroid(level, message);
    }

    public void trace(String message) { log(LogLevel.TRACE, message); }
    public void debug(String message) { log(LogLevel.DEBUG, message); }
    public void info(String message)  { log(LogLevel.INFO, message); }
    public void warn(String message)  { log(LogLevel.WARN, message); }
    public void error(String message) { log(LogLevel.ERROR, message); }
    public void fatal(String message) { log(LogLevel.FATAL, message); }

    public void traceAsync(String message) { kirceLogger.traceAsync(message); logToAndroid(LogLevel.TRACE, message); }
    public void debugAsync(String message) { kirceLogger.debugAsync(message); logToAndroid(LogLevel.DEBUG, message); }
    public void infoAsync(String message)  { kirceLogger.infoAsync(message);  logToAndroid(LogLevel.INFO, message); }
    public void warnAsync(String message)  { kirceLogger.warnAsync(message);  logToAndroid(LogLevel.WARN, message); }
    public void errorAsync(String message) { kirceLogger.errorAsync(message); logToAndroid(LogLevel.ERROR, message); }
    public void fatalAsync(String message) { kirceLogger.fatalAsync(message); logToAndroid(LogLevel.FATAL, message); }

    /**
     * Returns the underlying KirCELogger instance.
     */
    public KirCELogger getKirceLogger() {
        return kirceLogger;
    }
}