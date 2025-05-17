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
 * Represents accessing a field or property from an object in the AST.
 *
 * <p>Examples:
 * <pre>{@code
 *   this.name        // get field "name" from 'this'
 *   user.age         // get field "age" from variable 'user'
 *   config.debug     // get field "debug" from variable 'config'
 * }</pre>
 *
 * <p>This is a read operation, not an assignment.
 */
public record GetExpr(Expression target, String field) implements Expression {

    /**
     * Creates a new field access expression (e.g., obj.field).
     *
     * @param target The expression evaluating to the object (e.g., VariableExpr("user")).
     * @param field  The field name being accessed (e.g., "age").
     * @return a new GetExpr instance representing the field access.
     * @throws NullPointerException if target or field is null.
     */
    public static GetExpr of(Expression target, String field) {
        java.util.Objects.requireNonNull(target, "target must not be null");
        java.util.Objects.requireNonNull(field, "field must not be null");
        return new GetExpr(target, field);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for this expression type.
     *
     * @param visitor The visitor to process this AST node.
     * @param <R>     The return type of the visitor.
     * @return The result of the visitor's processing.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }

    /**
     * Returns a string representation of this field access expression.
     *
     * @return a string in the format "target.field"
     */
    @Override
    public String toString() {
        return target + "." + field;
    }
}
