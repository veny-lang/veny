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

import java.util.List;
import java.util.Objects;

/**
 * Represents a method or function call expression in the abstract syntax tree (AST).
 * <p>
 * The {@code callee} is the target of the call—it can be a simple function name
 * (such as a {@link VariableExpr}) or a more complex expression that evaluates
 * to a callable, like a method access via {@link GetExpr}.
 *
 * <p><strong>Examples:</strong>
 * <ul>
 *   <li><code>print("Hello")</code> → callee = {@code VariableExpr("print")}</li>
 *   <li><code>math.sqrt(9)</code> → callee = {@code GetExpr(VariableExpr("math"), "sqrt")}</li>
 * </ul>
 *
 * @param callee    The expression identifying the function or method being called.
 * @param arguments The list of argument expressions passed to the call.
 */
public record CallExpr(Expression callee, List<Expression> arguments) implements Expression {

    /**
     * Constructs a {@code CallExpr}, ensuring non-null fields.
     *
     * @throws NullPointerException if {@code callee} or {@code arguments} is {@code null}.
     */
    public CallExpr {
        Objects.requireNonNull(callee, "callee must not be null");
        Objects.requireNonNull(arguments, "arguments must not be null");
    }

    /**
     * Creates a new {@code CallExpr} with the given callee and argument list.
     *
     * @param callee    The target function or method.
     * @param arguments The list of argument expressions.
     * @return A new {@code CallExpr} instance.
     */
    public static CallExpr of(Expression callee, List<Expression> arguments) {
        return new CallExpr(callee, arguments);
    }

    /**
     * Creates a new {@code CallExpr} with the given callee and a varargs list of arguments.
     *
     * @param callee    The target function or method.
     * @param arguments Argument expressions passed to the callee.
     * @return A new {@code CallExpr} instance.
     */
    public static CallExpr of(Expression callee, Expression... arguments) {
        return new CallExpr(callee, List.of(arguments));
    }

    /**
     * Accepts a visitor that performs an operation on this call expression.
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
     * Returns a string representation of the call expression in source-like format.
     *
     * @return A string like {@code print("Hello")} or {@code obj.method(arg1, arg2)}.
     */
    @Override
    public String toString() {
        String args = arguments.stream()
                .map(Object::toString)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return callee + "(" + args + ")";
    }
}
