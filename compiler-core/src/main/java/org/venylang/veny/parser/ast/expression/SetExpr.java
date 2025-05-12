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

public record SetExpr(Expression target, String field, Expression value) implements Expression {

    /**
     * Constructs a field assignment expression (e.g., obj.field = value).
     *
     * @param target The object whose field is being assigned (e.g., VariableExpr("user")).
     * @param field  The field name (e.g., "age").
     * @param value  The value to assign to the field.
     */
    public SetExpr {
    }

    /**
     * @return the expression representing the object (left-hand side before the dot).
     */
    @Override public Expression target() {
        return target;
    }

    /**
     * @return the field name being assigned.
     */
    @Override public String field() {
        return field;
    }

    /**
     * @return the value being assigned to the field.
     */
    @Override public Expression value() {
        return value;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitSetExpr(this);
    }

    @Override
    public String toString() {
        return target + "." + field + " = " + value;
    }
}
