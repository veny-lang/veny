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
 * Represents a return statement in the AST.
 *
 * <p>Example:
 * <pre>{@code
 * return x + y;
 * }</pre>
 */
public record ReturnStmt(Expression value) implements Statement {

    /**
     * Creates a new return statement with the specified return value.
     *
     * @param value The expression whose value will be returned.
     * @return a new {@code ReturnStmt} instance.
     * @throws NullPointerException if {@code value} is null.
     */
    public static ReturnStmt of(Expression value) {
        Objects.requireNonNull(value, "value must not be null");
        return new ReturnStmt(value);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for return statements.
     *
     * @param visitor The visitor to process this AST node.
     * @param <R>     The return type of the visitor.
     * @return The result of the visitor's processing.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitReturnStmt(this);
    }

    /**
     * Returns a string representation of this return statement.
     *
     * @return a string like {@code return expression}
     */
    @Override
    public String toString() {
        return "return " + value;
    }
}
