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
 * Represents an if-else statement in the AST.
 *
 * <p>Examples:
 * <pre>{@code
 * if (x > 0) {
 *     print("Positive");
 * }
 *
 * if (x > 0) {
 *     print("Positive");
 * } else {
 *     print("Non-positive");
 * }
 * }</pre>
 */
public record IfStmt(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {

    /**
     * Creates a new if-else statement.
     *
     * @param condition   The condition to evaluate.
     * @param thenBranch  The statement to execute if the condition is true.
     * @param elseBranch  The statement to execute if the condition is false (nullable).
     * @return a new IfStmt instance.
     * @throws NullPointerException if condition or thenBranch is null.
     */
    public static IfStmt of(Expression condition, Statement thenBranch, Statement elseBranch) {
        Objects.requireNonNull(condition, "condition must not be null");
        Objects.requireNonNull(thenBranch, "thenBranch must not be null");
        return new IfStmt(condition, thenBranch, elseBranch);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for if-else statements.
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
     * Returns a string representation of this if-else statement.
     *
     * @return a formatted string like {@code if (condition) thenBranch [else elseBranch]}
     */
    @Override
    public String toString() {
        return "if (" + condition + ") " + thenBranch +
                (elseBranch != null ? " else " + elseBranch : "");
    }
}
