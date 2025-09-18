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

package org.venylang.veny.semantic.symbols;

import org.venylang.veny.semantic.Scope;
import org.venylang.veny.semantic.SemanticException;
import org.venylang.veny.semantic.Symbol;

/**
 * Represents a local scope, typically used for blocks or nested scopes inside methods.
 * Variables declared inside a LocalScope do not leak to enclosing scopes.
 */
public class LocalScope extends BaseScope {

    /**
     * Constructs a new local scope with the given enclosing scope.
     *
     * @param enclosingScope the scope that encloses this local scope
     */
    public LocalScope(Scope enclosingScope) {
        super(enclosingScope);
    }

    @Override
    public String getScopeName() {
        return "local";
    }

    /**
     * Optionally, you can override define() to prevent redefinition in the same local scope.
     */
    @Override
    public void define(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            throw new SemanticException(
                    "Symbol '" + symbol.getName() + "' already declared in scope '" + getScopeName() + "'"
            );
        }
        super.define(symbol);
    }
}
