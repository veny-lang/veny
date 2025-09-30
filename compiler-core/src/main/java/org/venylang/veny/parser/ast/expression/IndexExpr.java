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
 * Represents an array or indexed access expression in the AST.
 * <p>
 * Example usages:
 * <ul>
 *     <li>{@code arr[0]}</li>
 *     <li>{@code matrix[i][j]}</li>
 * </ul>
 * The target expression must evaluate to an array or an indexable type,
 * and the index expression must evaluate to an integer type.
 */
public class IndexExpr extends Expression {

    private final Expression target;
    private final Expression index;

    /**
     * Creates a new {@code IndexExpr}.
     *
     * @param target The expression being indexed (e.g., a variable or array reference).
     * @param index  The index expression (e.g., a literal or variable).
     * @throws NullPointerException if {@code target} or {@code index} is {@code null}.
     */
    public IndexExpr(Expression target, Expression index) {
        this.target = Objects.requireNonNull(target, "target must not be null");
        this.index = Objects.requireNonNull(index, "index must not be null");
    }

    /**
     * Factory method for creating an {@code IndexExpr}.
     *
     * @param target The target expression.
     * @param index  The index expression.
     * @return A new {@code IndexExpr} instance.
     */
    public static IndexExpr of(Expression target, Expression index) {
        return new IndexExpr(target, index);
    }

    /**
     * Returns the expression being indexed (e.g., {@code arr} in {@code arr[0]}).
     *
     * @return The target expression.
     */
    public Expression target() {
        return target;
    }

    /**
     * Returns the index expression (e.g., {@code 0} in {@code arr[0]}).
     *
     * @return The index expression.
     */
    public Expression index() {
        return index;
    }

    /**
     * Accepts a visitor, dispatching to the visitor's {@link AstVisitor#visit(IndexExpr)} method.
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
     * Returns a string representation of this index expression in source-like form.
     *
     * @return A string in the form {@code target[index]}.
     */
    @Override
    public String toString() {
        return target + "[" + index + "]";
    }
}