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

package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

import java.util.Objects;

/**
 * Represents a unary expression in the AST, applying an operator to a single operand.
 *
 * <p>Examples:
 * <pre>{@code
 *   -x
 *   !flag
 * }</pre>
 */
public record UnaryExpr(String operator, Expression operand) implements Expression {

    /**
     * Creates a new unary expression.
     *
     * @param operator The unary operator (e.g., "-", "!").
     * @param operand  The expression the operator is applied to.
     * @return a new UnaryExpr instance representing the unary operation.
     * @throws NullPointerException if operator or operand is null.
     */
    public static UnaryExpr of(String operator, Expression operand) {
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(operand, "operand must not be null");
        return new UnaryExpr(operator, operand);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for unary expressions.
     *
     * @param visitor The visitor to process this AST node.
     * @param <R>     The return type of the visitor.
     * @return The result of the visitor's processing.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }

    /**
     * Returns a string representation of this unary expression.
     *
     * @return a string in the format "(operator operand)"
     */
    @Override
    public String toString() {
        return "(" + operator + operand + ")";
    }
}

