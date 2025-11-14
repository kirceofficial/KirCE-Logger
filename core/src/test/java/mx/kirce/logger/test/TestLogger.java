/*
 * Copyright 2025 KirCE (KirCE Logger)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package mx.kirce.logger.test;

import mx.kirce.logger.KirCELogger;
import mx.kirce.logger.LogLevel;
import mx.kirce.logger.handle.ConsoleLogHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for {@link KirCELogger}.
 *
 * <p>Verifies that all log levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
 * can be logged without throwing exceptions.</p>
 *
 * <pre>
 * KirCELogger logger = new KirCELogger("TestLogger");
 * logger.addHandler(new ConsoleLogHandler(true)); // enable colors
 * logger.info("Info message");
 * logger.warn("Warn message");
 * logger.error("Error message");
 * logger.fatal("Fatal message");
 * logger.debug("Debug message");
 * logger.trace("Trace message");
 * </pre>
 */
public class TestLogger {

    /**
     * Tests all log levels for proper execution without exceptions.
     */
    @Test
    public void testAllLogLevels() {
        KirCELogger logger = new KirCELogger("TestLogger");
        logger.addHandler(new ConsoleLogHandler(true));
        KirCELogger.setGlobalMinLevel(LogLevel.INFO);

        assertDoesNotThrow(() -> logger.info("Info message"));
        assertDoesNotThrow(() -> logger.warn("Warn message"));
        assertDoesNotThrow(() -> logger.error("Error message"));
        assertDoesNotThrow(() -> logger.fatal("Fatal message"));
        assertDoesNotThrow(() -> logger.debug("Debug message"));
        assertDoesNotThrow(() -> logger.trace("Trace message"));
    }
}