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

import java.util.List;
import java.util.Objects;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.semantic.Type;

/**
 * Represents an array literal expression in the abstract syntax tree (AST).
 * <p>
 * An array literal is a fixed-size collection of values defined directly in the code,
 * such as <code>[1, 2, 3]</code>. This expression holds a list of sub-expressions that
 * define the array's elements.
 *
 * @param elements The list of expressions representing the elements of the array.
 */
public class ArrayLiteralExpr implements Expression {
    private final List<Expression> elements;
    private Type elementType; // mutable so semantic analyzer can fill it in

    /**
     * Creates a new {@code ArrayLiteralExpr} with the given elements.
     *
     * @param elements A list of expressions representing the elements of the array.
     * @param elementType The type of the array element
     * @return A new {@code ArrayLiteralExpr} instance.
     */
    public static ArrayLiteralExpr of(List<Expression> elements, Type elementType) {
        return new ArrayLiteralExpr(elements, elementType);
    }

    public ArrayLiteralExpr(List<Expression> elements, Type elementType) {
        this.elements = Objects.requireNonNull(elements, "elements must not be null");
        this.elementType = elementType; // may be null initially, filled later
    }

    public List<Expression> elements() {
        return elements;
    }

    public Type elementType() {
        return elementType;
    }

    public void elementType(Type elementType) {
        this.elementType = elementType;
    }
    /**
     * Accepts a visitor that performs an operation on this array literal expression.
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
     * Returns a string representation of the array literal expression,
     * including its elements.
     *
     * @return A string showing the array elements in source-like format.
     */
    @Override
    public String toString() {
        return "[" + elements.stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse("") + "]";
    }
}

