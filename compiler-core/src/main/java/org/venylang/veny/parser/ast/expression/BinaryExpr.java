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

/**
 * Represents a binary expression in the abstract syntax tree (AST).
 * <p>
 * A binary expression consists of a left and right expression with a binary operator,
 * such as <code>a + b</code> or <code>x == y</code>.
 *
 * @param left     The left-hand side expression.
 * @param operator The binary operator (e.g., "+", "-", "*", "==").
 * @param right    The right-hand side expression.
 */
public record BinaryExpr(Expression left, String operator, Expression right) implements Expression {

    /**
     * Creates a {@code BinaryExpr} instance with the given operands and operator.
     * <p>
     * This method validates the operator and throws an exception if it is invalid.
     *
     * @param left     The left-hand side expression.
     * @param operator The binary operator (e.g., "+", "==").
     * @param right    The right-hand side expression.
     * @return A new {@code BinaryExpr} instance.
     * @throws IllegalArgumentException if the operator is null or blank.
     */
    public static BinaryExpr of(Expression left, String operator, Expression right) {
        if (operator == null || operator.isBlank()) {
            throw new IllegalArgumentException("Operator must not be null or blank");
        }
        return new BinaryExpr(left, operator, right);
    }

    /**
     * Accepts a visitor that performs an operation on this binary expression.
     *
     * @param visitor The AST visitor.
     * @param <R>     The return type of the visitor.
     * @return The result of visiting this node.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns a string representation of the binary expression in source-like format.
     *
     * @return A string in the form "(left operator right)".
     */
    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
