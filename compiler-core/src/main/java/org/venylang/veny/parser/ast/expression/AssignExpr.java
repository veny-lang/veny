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
public record AssignExpr(String name, Expression value) implements Expression {

    /**
     * Constructs an assignment expression.
     *
     * @param name  The variable name being assigned to.
     * @param value The expression representing the assigned value.
     */
    public AssignExpr {
    }

    /**
     * Gets the variable name.
     *
     * @return the name of the variable.
     */
    @Override public String name() {
        return name;
    }

    /**
     * Gets the assigned value expression.
     *
     * @return the expression on the right-hand side of the assignment.
     */
    @Override public Expression value() {
        return value;
    }


    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
