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
 * Represents a field assignment expression in the AST.
 *
 * <p>Example:
 * <pre>{@code
 *   user.age = 30
 * }</pre>
 */
public record SetExpr(Expression target, String field, Expression value) implements Expression {

    /**
     * Creates a new field assignment expression (e.g., obj.field = value).
     *
     * @param target The object whose field is being assigned (e.g., VariableExpr("user")).
     * @param field  The field name (e.g., "age").
     * @param value  The value to assign to the field.
     * @return a new SetExpr instance representing the assignment.
     * @throws NullPointerException if target, field, or value is null.
     */
    public static SetExpr of(Expression target, String field, Expression value) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(value, "value must not be null");
        return new SetExpr(target, field, value);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for set expressions.
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
     * Returns a string representation of this field assignment expression.
     *
     * @return a string in the format "target.field = value"
     */
    @Override
    public String toString() {
        return target + "." + field + " = " + value;
    }
}
