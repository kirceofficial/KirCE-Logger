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

/**
 * Enum representing different log levels for KirCE Logger.
 * 
 * <p>Log levels determine the severity and type of log messages.
 * They can be used to filter and categorize logs in your application.</p>
 * 
 * <ul>
 *   <li><b>TRACE</b> - Fine-grained informational events, mostly used for debugging.</li>
 *   <li><b>DEBUG</b> - General debugging information.</li>
 *   <li><b>INFO</b>  - Informational messages that highlight the progress of the application.</li>
 *   <li><b>WARN</b>  - Potentially harmful situations or warnings.</li>
 *   <li><b>ERROR</b> - Error events that might still allow the application to continue.</li>
 *   <li><b>FATAL</b> - Very severe error events that will likely lead to application termination.</li>
 * </ul>
 */
public enum LogLevel {
    /** Fine-grained trace messages for debugging. */
    TRACE, 
    
    /** General debugging information. */
    DEBUG, 
    
    /** Informational messages to indicate normal operation. */
    INFO, 
    
    /** Warnings for potentially problematic situations. */
    WARN, 
    
    /** Errors that do not stop the application. */
    ERROR, 
    
    /** Severe errors likely leading to application termination. */
    FATAL
}