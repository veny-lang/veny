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

import java.util.List;

import java.util.Objects;

/**
 * Represents a class declaration in the Veny language AST.
 * <p>
 * A class consists of a name, optional superclass, a list of interfaces,
 * a list of field declarations, and a list of method declarations.
 *
 * <p>Example:
 * <pre>
 * class Dog extends Animal implements Pet, Runnable {
 *     val name: String
 *     fun bark() { ... }
 * }
 * </pre>
 *
 * @param name       The name of the class.
 * @param parent     The name of the parent class (null if none).
 * @param interfaces The list of implemented interfaces.
 * @param fields     The list of field declarations (both val and var).
 * @param methods    The list of method declarations.
 */
public record ClassDecl(
        String name,
        String parent,              // e.g., "Animal"
        List<String> interfaces,        // e.g., ["Pet", "Runnable"]
        List<VarDecl> fields,
        List<MethodDecl> methods
) implements AstNode {

    /**
     * Constructs a new {@code ClassDecl} instance with the given name, superclass, interfaces, fields, and methods.
     *
     * @param name       the name of the class
     * @param parent     the optional parent class name (can be null)
     * @param interfaces the list of implemented interfaces (non-null)
     * @param fields     the list of field declarations
     * @param methods    the list of method declarations
     * @return a new {@code ClassDecl} instance
     * @throws NullPointerException if name, interfaces, fields, or methods are {@code null}
     */
    public static ClassDecl of(
            String name,
            String parent,
            List<String> interfaces,
            List<VarDecl> fields,
            List<MethodDecl> methods
    ) {
        Objects.requireNonNull(name, "Class name must not be null");
        Objects.requireNonNull(interfaces, "Interfaces must not be null");
        Objects.requireNonNull(fields, "Fields must not be null");
        Objects.requireNonNull(methods, "Methods must not be null");
        return new ClassDecl(name, parent, interfaces, fields, methods);
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitClassDecl(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("class " + name);
        if (parent != null) {
            sb.append(" extends ").append(parent);
        }
        if (!interfaces.isEmpty()) {
            sb.append(" implements ").append(String.join(", ", interfaces));
        }
        sb.append(" {\n");
        sb.append("  fields: ").append(fields).append("\n");
        sb.append("  methods: ").append(methods).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
