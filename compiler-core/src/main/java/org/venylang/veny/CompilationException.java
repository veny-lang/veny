/*
 * Copyright 2025 Stoyan Petkov
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
 *
 */

package org.venylang.veny;

/**
 * Thrown to indicate a compile-time error in the Veny compiler.
 */
public class CompilationException extends RuntimeException {

    /**
     * Constructs a new CompilationException with the specified detail message.
     *
     * @param message the detail message
     */
    public CompilationException(String message) {
        super(message);
    }

    /**
     * Constructs a new CompilationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CompilationException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public CompilationException(Throwable cause) {
        super(cause);
    }
}
