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
 * Represents a while-loop statement in the AST.
 *
 * <p>Examples:
 * <pre>{@code
 * while (counter < 10) {
 *     print(counter);
 *     counter = counter + 1;
 * }
 * }</pre>
 *
 * The loop continues as long as the condition evaluates to true.
 */
public record WhileStmt(Expression condition, BlockStmt body) implements Statement {

    /**
     * Creates a new {@code WhileStmt}.
     *
     * @param condition The loop condition expression.
     * @param body      The block to execute while the condition is true.
     * @return a new instance of {@code WhileStmt}
     * @throws NullPointerException if {@code condition} or {@code body} is null.
     */
    public static WhileStmt of(Expression condition, BlockStmt body) {
        Objects.requireNonNull(condition, "condition must not be null");
        Objects.requireNonNull(body, "body must not be null");
        return new WhileStmt(condition, body);
    }

    /**
     * Accepts a visitor for AST traversal.
     *
     * @param visitor The visitor to process this statement.
     * @param <R>     The result type of the visitor.
     * @return The result of visiting this statement.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns a string representation of the while-loop.
     *
     * @return a string like {@code while (condition) { ... }}
     */
    @Override
    public String toString() {
        return "while (" + condition + ") " + body;
    }
}
