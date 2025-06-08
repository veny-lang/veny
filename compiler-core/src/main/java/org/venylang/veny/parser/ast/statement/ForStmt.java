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
 * Represents a for-loop statement in the AST.
 *
 * <p>Example:
 * <pre>{@code
 * for item in collection {
 *     // loop body
 * }
 * }</pre>
 */
public record ForStmt(String variable, Expression iterable, BlockStmt body) implements Statement {

    /**
     * Creates a new for-loop statement.
     *
     * @param variable The loop variable name.
     * @param iterable The expression representing the collection or iterable.
     * @param body     The block of statements to execute for each iteration.
     * @return a new ForStmt instance.
     * @throws NullPointerException if any argument is null.
     */
    public static ForStmt of(String variable, Expression iterable, BlockStmt body) {
        Objects.requireNonNull(variable, "variable must not be null");
        Objects.requireNonNull(iterable, "iterable must not be null");
        Objects.requireNonNull(body, "body must not be null");
        return new ForStmt(variable, iterable, body);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for for-loop statements.
     *
     * @param visitor The visitor to process this AST node.
     * @param <R>     The return type of the visitor.
     * @return The result of the visitor's processing.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns a string representation of this for-loop statement.
     *
     * @return a string in the format "for variable in iterable body"
     */
    @Override
    public String toString() {
        return "for " + variable + " in " + iterable + " " + body;
    }
}
