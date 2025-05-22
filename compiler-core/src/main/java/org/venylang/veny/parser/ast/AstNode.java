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

/**
 * The base interface for all nodes in the Abstract Syntax Tree (AST).
 * <p>
 * Every AST node must implement this interface, allowing it to accept an
 * {@link AstVisitor} for traversing or processing the tree using the Visitor pattern.
 * </p>
 *
 * @param <R> The return type produced by the visitor.
 */
public interface AstNode {

    /**
     * Accepts a visitor that processes this AST node.
     *
     * @param visitor The visitor to accept.
     * @param <R>     The return type of the visitor's processing logic.
     * @return The result of the visitor's processing.
     */
    <R> R accept(AstVisitor<R> visitor);
}
