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
 * Represents an assignment expression in the AST.
 *
 * Examples:
 * <pre>
 *   x = 42
 *   name = "Alice"
 *   flag = true
 *   radius = radius + 1.0
 * </pre>
 *
 * This class handles only simple variable assignments (not object field or array element assignments).
 * For object fields (e.g., obj.field = value), use {@link SetExpr}.
 */
public class AssignExpr extends Expression {
    private final String name;
    private final Expression value;

    /**
     * Constructs an assignment expression.
     *
     * @param name  The variable name being assigned to.
     * @param value The expression representing the assigned value.
     */
    public AssignExpr(String name, Expression value) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Constructs an assignment expression.
     *
     * @param name  The variable name being assigned to.
     * @param value The expression representing the assigned value.
     */
    public static AssignExpr of(String name, Expression value) {
        return new AssignExpr(name, value);
    }

    public String name() {
        return name;
    }

    public Expression value() {
        return value;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
