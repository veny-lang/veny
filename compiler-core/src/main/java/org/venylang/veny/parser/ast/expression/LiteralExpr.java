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
import org.venylang.veny.semantic.Type;
import org.venylang.veny.semantic.types.BuiltinType;

import java.util.Objects;

/**
 * Represents a literal value in the AST, such as a number, string, or boolean.
 */
public class LiteralExpr extends Expression {
    private final Object value;
    private final Type type;

    /**
     * Constructs a new LiteralExpr with the given value and type.
     *
     * @param value the literal value (Integer, String, Boolean, etc.)
     * @param type  the resolved type for this literal
     */
    public LiteralExpr(Object value, Type type) {
        this.value = value;
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.setResolvedType(type); // sync with Expression's resolvedType
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    /**
    * Creates a new literal expression with the given value.
    *
    * @param value The literal value, e.g., an Integer, String, Boolean, etc.
    * @return a new LiteralExpr instance wrapping the value.
    */
    public static LiteralExpr of(Object value) {
    Objects.requireNonNull(value, "value must not be null");
    if (value instanceof Integer) {
        return new LiteralExpr(value, BuiltinType.INT);
    }
    if (value instanceof Boolean) {
        return new LiteralExpr(value, BuiltinType.BOOL);
    }
    if (value instanceof String) {
        return new LiteralExpr(value, BuiltinType.TEXT);
    }
    if (value == null) {
        return new LiteralExpr(null, BuiltinType.NULL);
    }
    throw new IllegalArgumentException("Unsupported literal type: " + value);
    }

    /**
    * Accepts a visitor, dispatching to the visitor's method for literal expressions.
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
    * Returns a string representation of this literal expression.
    *
    * @return a string in the format "LiteralExpr(value)"
    */
    @Override
    public String toString() {
    return "LiteralExpr(" + value + " : " + type + ")";
    }
}
