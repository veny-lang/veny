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
 * Represents a "new" object instantiation expression in the AST.
 *
 * <p>Example:
 * <pre>{@code
 *   new MyClass(arg1, arg2)
 * }</pre>
 */
public record NewExpr(String className, List<Expression> arguments) implements Expression {

    /**
     * Creates a new object instantiation expression.
     *
     * @param className The name of the class to instantiate.
     * @param arguments The list of argument expressions passed to the constructor.
     * @return a new NewExpr instance representing the instantiation.
     * @throws NullPointerException if className or arguments is null.
     */
    public static NewExpr of(String className, List<Expression> arguments) {
        Objects.requireNonNull(className, "className must not be null");
        Objects.requireNonNull(arguments, "arguments must not be null");
        return new NewExpr(className, arguments);
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for new expressions.
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
     * Returns a string representation of this new expression.
     *
     * @return a string in the format "new ClassName(arg1, arg2, ...)"
     */
    @Override
    public String toString() {
        return "new " + className + arguments;
    }
}
