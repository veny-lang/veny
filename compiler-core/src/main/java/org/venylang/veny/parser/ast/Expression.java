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

import org.venylang.veny.semantic.Type;

/**
 * Represents an expression node in the Veny language abstract syntax tree.
 * <p>
 * This may include literals (e.g., numbers, strings), variable accesses,
 * binary operations, method calls, or more complex constructs.
 * All expressions are also {@link AstNode}s and participate in tree traversal,
 * analysis, and code generation.
 */
public abstract class Expression implements AstNode {
    private Type resolvedType; // null until semantic analysis fills it

    public Type getResolvedType() {
        return resolvedType;
    }

    public void setResolvedType(Type resolvedType) {
        this.resolvedType = resolvedType;
    }
}
