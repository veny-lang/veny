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

package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

import java.util.Objects;

/**
 * Represents an immutable variable declaration (`val`) in the AST.
 *
 * <p>Examples:
 * <pre>{@code
 * val x: Int = 42;
 * val name: String;
 * }</pre>
 *
 * A `val` defines a read-only variable. The type is always explicit in Veny.
 */
public record ValStmt(String name, String type, Expression initializer) implements Statement {

    /**
     * Creates a new immutable variable declaration.
     *
     * @param name        The name of the variable.
     * @param type        The declared type of the variable.
     * @param initializer The expression used to initialize the variable (nullable).
     * @return A new {@code ValStmt} instance.
     * @throws NullPointerException if {@code name} or {@code type} is null.
     */
    public static ValStmt of(String name, String type, Expression initializer) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");
        return new ValStmt(name, type, initializer);
    }

    /**
     * Accepts a visitor to process this `val` statement.
     *
     * @param visitor The AST visitor.
     * @param <R>     The visitor's return type.
     * @return The result of the visitor's operation.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitValStmt(this);
    }

    /**
     * Returns a string representation of this `val` statement.
     *
     * @return a formatted string like {@code val name: Type = value}
     */
    @Override
    public String toString() {
        return "val " + name + ": " + type + (initializer != null ? " = " + initializer : "");
    }
}
