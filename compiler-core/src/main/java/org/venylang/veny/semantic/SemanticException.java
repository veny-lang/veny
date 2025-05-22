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

package org.venylang.veny.semantic;

/**
 * Exception thrown to indicate a semantic error during analysis of the Veny program.
 * <p>
 * This exception is typically used to report issues such as type mismatches,
 * use of undeclared variables, or invalid operations that are semantically incorrect
 * even though they are syntactically valid.
 * </p>
 */
public class SemanticException extends RuntimeException {

    /**
     * Constructs a new {@code SemanticException} with the specified detail message.
     *
     * @param message the detail message explaining the semantic error
     */
    public SemanticException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code SemanticException} with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the semantic error
     * @param cause   the cause of the exception (can be {@code null})
     */
    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }
}
