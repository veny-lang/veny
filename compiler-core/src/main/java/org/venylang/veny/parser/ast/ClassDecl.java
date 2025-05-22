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

import java.util.List;
import java.util.Objects;

/**
 * Represents a class declaration in the Veny language AST.
 * <p>
 * A class consists of a name, a list of field declarations, and a list of method declarations.
 *
 * <p>Example:
 * <pre>
 * class Person {
 *     val name: String
 *     var age: Int
 *     fun greet() { ... }
 * }
 * </pre>
 *
 * @param name    The name of the class.
 * @param fields  The list of field declarations (both val and var).
 * @param methods The list of method declarations.
 */
public record ClassDecl(String name, List<VarDecl> fields, List<MethodDecl> methods) implements AstNode {

    /**
     * Constructs a new {@code ClassDecl} instance with the given name, fields, and methods.
     *
     * @param name    the name of the class
     * @param fields  the list of field declarations
     * @param methods the list of method declarations
     * @return a new {@code ClassDecl} instance
     * @throws NullPointerException if any argument is {@code null}
     */
    public static ClassDecl of(String name, List<VarDecl> fields, List<MethodDecl> methods) {
        Objects.requireNonNull(name, "Class name must not be null");
        Objects.requireNonNull(fields, "Fields must not be null");
        Objects.requireNonNull(methods, "Methods must not be null");
        return new ClassDecl(name, fields, methods);
    }

    /**
     * Accepts a visitor that processes this class declaration.
     *
     * @param visitor the AST visitor
     * @return the result of the visitor's processing
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitClassDecl(this);
    }

    /**
     * Returns a string representation of the class declaration.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return "class " + name + " {\n" +
                "  fields: " + fields + "\n" +
                "  methods: " + methods + "\n" +
                "}";
    }
}
