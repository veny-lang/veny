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
 * Represents a variable reference expression in the AST.
 *
 * <p>Example:
 * <pre>{@code
 *   x
 *   myVariable
 * }</pre>
 */
public class VariableExpr extends Expression {
    private final String name;

    /**
     * Creates a new variable expression.
     *
     * @param name The name of the variable.
     * @throws NullPointerException if name is null.
     */
    public VariableExpr(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String name() {
        return name;
    }

    /**
     * Creates a new variable expression.
     *
     * @param name The name of the variable.
     * @return a new VariableExpr instance representing the variable.
     * @throws NullPointerException if name is null.
     */
    public static VariableExpr of(String name) {
        Objects.requireNonNull(name, "name must not be null");
        return new VariableExpr(name);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for variable expressions.
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
     * Returns a string representation of this variable expression.
     *
     * @return a string in the format "VariableExpr(name)"
     */
    @Override
    public String toString() {
        return "VariableExpr(" + name + ")";
    }
}
