/*
 * Copyright 2026 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.android

import android.util.Log
import mx.kirce.logger.KirCELogger
import mx.kirce.logger.LogLevel
import mx.kirce.logger.handle.LogHandler

/**
 * AndroidLogger integrates KirCE Logger with Android Logcat.
 *
 * Log messages are sent to both KirCE Logger handlers and Android Logcat.
 * Supports context, colors, asynchronous logging, and VERBOSE level.
 *
 * Example usage:
 * ```
 * val logger = AndroidLogger("Main")
 * logger.putContext("userId", 42)
 * logger.verbose("Verbose message example")
 * logger.info("Hello with context!")
 * ```
 */
class AndroidLogger(private val tag: String) {

    private val kirceLogger: KirCELogger = KirCELogger(tag)

    /** Enables or disables colored log output for this logger instance. */
    fun enableColors(enable: Boolean) {
        kirceLogger.enableColors(enable)
    }

    /** Adds a custom log handler to this logger instance. */
    fun addHandler(handler: LogHandler?) {
        if (handler != null) kirceLogger.addHandler(handler)
    }

    /** Adds or updates a context value for this logger. */
    fun putContext(key: String, value: Any) {
        kirceLogger.putContext(key, value)
    }

    /** Removes a context value by key. */
    fun removeContext(key: String) {
        kirceLogger.removeContext(key)
    }

    /** Returns an unmodifiable map of the current context values. */
    fun getContext(): Map<String, Any> = kirceLogger.getContext()

    /** Sends a log message to Android Logcat using the appropriate Log level. */
    private fun logToAndroid(level: LogLevel, message: String) {
        when (level) {
            LogLevel.VERBOSE, LogLevel.TRACE -> Log.v(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARN -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
            LogLevel.FATAL -> Log.wtf(tag, message)
        }
    }

    /** Logs a message asynchronously to KirCE Logger and Android Logcat. */
    private fun log(level: LogLevel, message: String) {
        kirceLogger.logAsync(level, message)
        logToAndroid(level, message)
    }

    /** Logs a VERBOSE message. */
    fun verbose(message: String) = log(LogLevel.VERBOSE, message)

    /** Logs a TRACE message. */
    fun trace(message: String) = log(LogLevel.TRACE, message)

    /** Logs a DEBUG message. */
    fun debug(message: String) = log(LogLevel.DEBUG, message)

    /** Logs an INFO message. */
    fun info(message: String) = log(LogLevel.INFO, message)

    /** Logs a WARN message. */
    fun warn(message: String) = log(LogLevel.WARN, message)

    /** Logs an ERROR message. */
    fun error(message: String) = log(LogLevel.ERROR, message)

    /** Logs a FATAL message. */
    fun fatal(message: String) = log(LogLevel.FATAL, message)

    /** Logs a VERBOSE message asynchronously. */
    fun verboseAsync(message: String) {
        kirceLogger.logAsync(LogLevel.VERBOSE, message)
        logToAndroid(LogLevel.VERBOSE, message)
    }

    /** Logs a TRACE message asynchronously. */
    fun traceAsync(message: String) {
        kirceLogger.traceAsync(message)
        logToAndroid(LogLevel.TRACE, message)
    }

    /** Logs a DEBUG message asynchronously. */
    fun debugAsync(message: String) {
        kirceLogger.debugAsync(message)
        logToAndroid(LogLevel.DEBUG, message)
    }

    /** Logs an INFO message asynchronously. */
    fun infoAsync(message: String) {
        kirceLogger.infoAsync(message)
        logToAndroid(LogLevel.INFO, message)
    }

    /** Logs a WARN message asynchronously. */
    fun warnAsync(message: String) {
        kirceLogger.warnAsync(message)
        logToAndroid(LogLevel.WARN, message)
    }

    /** Logs an ERROR message asynchronously. */
    fun errorAsync(message: String) {
        kirceLogger.errorAsync(message)
        logToAndroid(LogLevel.ERROR, message)
    }

    /** Logs a FATAL message asynchronously. */
    fun fatalAsync(message: String) {
        kirceLogger.fatalAsync(message)
        logToAndroid(LogLevel.FATAL, message)
    }

    /** Returns the underlying KirCELogger instance. */
    fun getKirceLogger(): KirCELogger = kirceLogger
}