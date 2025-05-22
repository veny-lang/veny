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

package org.venylang.veny.parser.ast;

import org.venylang.veny.util.Visibility;

import java.util.Objects;

/**
 * Represents a variable declaration in the AST.
 *
 * @param name        The variable name.
 * @param typeName    The type of the variable as a string.
 * @param initializer The expression assigned to initialize the variable (may be null).
 * @param isMutable   Whether the variable is mutable (`var`) or immutable (`val`).
 * @param visibility  The visibility modifier (e.g., public, private).
 */
public record VarDecl(
        String name,
        String typeName,
        Expression initializer,
        boolean isMutable,
        Visibility visibility
) implements AstNode {

    /**
     * Creates a new VarDecl instance.
     *
     * @param name        the variable name, must not be null
     * @param typeName    the variable type name, must not be null
     * @param initializer the initializer expression, can be null if no initializer
     * @param isMutable   true if mutable (var), false if immutable (val)
     * @param visibility  the visibility modifier, must not be null
     * @return a new VarDecl instance
     * @throws NullPointerException if name, typeName, or visibility is null
     */
    public static VarDecl of(String name, String typeName, Expression initializer,
                             boolean isMutable, Visibility visibility) {
        Objects.requireNonNull(name, "Variable name must not be null");
        Objects.requireNonNull(typeName, "Variable typeName must not be null");
        Objects.requireNonNull(visibility, "Visibility must not be null");
        return new VarDecl(name, typeName, initializer, isMutable, visibility);
    }

    /**
     * Accepts a visitor for this variable declaration.
     *
     * @param visitor the AST visitor
     * @param <R>     the return type of the visitor
     * @return the visitor's result
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVarDecl(this);
    }

    /**
     * Returns a string representation of this variable declaration.
     *
     * @return string representation like "public var x: Int = 5"
     */
    @Override
    public String toString() {
        String mutability = isMutable ? "var" : "val";
        String init = initializer != null ? " = " + initializer : "";
        return visibility + " " + mutability + " " + name + ": " + typeName + init;
    }
}
