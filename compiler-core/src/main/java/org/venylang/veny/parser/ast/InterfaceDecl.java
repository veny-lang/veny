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
 * Represents an interface declaration in the Veny language AST.
 * <p>
 * An interface consists of a name, an optional list of parent interfaces,
 * and a list of method signatures that must be implemented by classes.
 *
 * <p>Example:
 * <pre>
 * interface Pet {
 *     play(): void
 * }
 *
 * interface RobotPet extends Pet {
 *     charge(): void
 * }
 * </pre>
 *
 * @param name     The name of the interface.
 * @param parents  The list of interfaces this interface extends (can be empty).
 * @param methods  The list of method declarations (signatures only).
 */
public record InterfaceDecl(
        String name,
        List<String> parents,
        List<MethodDecl> methods
) implements AstNode {

    /**
     * Constructs a new {@code InterfaceDecl} instance.
     *
     * @param name     the interface name
     * @param parents  list of extended interfaces
     * @param methods  list of method declarations
     * @return a new InterfaceDecl instance
     * @throws NullPointerException if name, parents, or methods is {@code null}
     */
    public static InterfaceDecl of(
            String name,
            List<String> parents,
            List<MethodDecl> methods
    ) {
        Objects.requireNonNull(name, "Interface name must not be null");
        Objects.requireNonNull(parents, "Parent interfaces must not be null");
        Objects.requireNonNull(methods, "Methods must not be null");
        return new InterfaceDecl(name, parents, methods);
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("interface " + name);
        if (!parents.isEmpty()) {
            sb.append(" extends ").append(String.join(", ", parents));
        }
        sb.append(" {\n");
        sb.append("  methods: ").append(methods).append("\n");
        sb.append("}");
        return sb.toString();
    }
}

