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
 * Represents a mutable variable declaration (`var`) in the AST.
 *
 * <p>Examples:
 * <pre>{@code
 * var count: Int = 0;
 * var message: String;
 * }</pre>
 *
 * A `var` defines a mutable variable. The type must be explicitly declared.
 */
public record VarStmt(String name, String type, Expression initializer) implements Statement {

    /**
     * Creates a new mutable variable declaration.
     *
     * @param name        The variable's name.
     * @param type        The declared type of the variable.
     * @param initializer The initializing expression, or {@code null} if uninitialized.
     * @return A {@code VarStmt} instance.
     * @throws NullPointerException if {@code name} or {@code type} is null.
     */
    public static VarStmt of(String name, String type, Expression initializer) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");
        return new VarStmt(name, type, initializer);
    }

    /**
     * Accepts an AST visitor.
     *
     * @param visitor The visitor processing this statement.
     * @param <R>     The result type of the visitor.
     * @return The result from the visitor.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVarStmt(this);
    }

    /**
     * Returns a string representation of this `var` statement.
     *
     * @return a formatted string like {@code var name: Type = value}
     */
    @Override
    public String toString() {
        return "var " + name + ": " + type + (initializer != null ? " = " + initializer : "");
    }
}
