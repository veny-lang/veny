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

import java.util.List;

import java.util.List;
import java.util.Objects;

/**
 * Represents a method declaration in the Veny language AST.
 * <p>
 * A method has a name, parameters, a return type, a body (list of statements),
 * and a visibility modifier.
 *
 * <p>Example:
 * <pre>
 * fun greet(name: String): Void {
 *     System.print("Hello " + name)
 * }
 * </pre>
 *
 * @param name       The method name.
 * @param parameters The list of parameters.
 * @param returnType The return type as a string.
 * @param body       The list of statements in the method body.
 * @param visibility The visibility modifier (e.g., public, private).
 */
public record MethodDecl(
        String name,
        List<Parameter> parameters,
        String returnType,
        List<Statement> body,
        Visibility visibility
) implements AstNode {

    /**
     * Represents a method parameter with a name and type.
     *
     * @param name the parameter name
     * @param type the parameter type
     */
    public record Parameter(String name, String type) {
        /**
         * Constructs a parameter.
         *
         * @param name the parameter name
         * @param type the parameter type
         * @throws NullPointerException if name or type is null
         */
        public Parameter {
            Objects.requireNonNull(name, "Parameter name must not be null");
            Objects.requireNonNull(type, "Parameter type must not be null");
        }

        @Override
        public String toString() {
            return name + ": " + type;
        }
    }

    /**
     * Constructs a new MethodDecl instance.
     *
     * @param name       the method name
     * @param parameters the list of parameters
     * @param returnType the return type
     * @param body       the method body statements
     * @param visibility the method visibility
     * @return a new MethodDecl instance
     * @throws NullPointerException if any argument is null
     */
    public static MethodDecl of(
            String name,
            List<Parameter> parameters,
            String returnType,
            List<Statement> body,
            Visibility visibility
    ) {
        Objects.requireNonNull(name, "Method name must not be null");
        Objects.requireNonNull(parameters, "Parameters must not be null");
        Objects.requireNonNull(returnType, "Return type must not be null");
        Objects.requireNonNull(body, "Body must not be null");
        Objects.requireNonNull(visibility, "Visibility must not be null");

        return new MethodDecl(name, parameters, returnType, body, visibility);
    }

    /**
     * Accepts a visitor for this method declaration.
     *
     * @param visitor the AST visitor
     * @param <R>     the return type of the visitor
     * @return the visitor's result
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitMethodDecl(this);
    }

    /**
     * Returns a string representation of the method declaration.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return visibility + " fun " + name + "(" + parameters + "): " + returnType + " " + body;
    }
}
