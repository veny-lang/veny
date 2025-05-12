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

package org.venylang.veny.semantic;

import java.util.Collection;

/**
 * Represents a lexical scope in the Lumina language.
 * Scopes are used to define and resolve symbols (variables, methods, classes, etc.).
 */
public interface Scope {

    /**
     * Defines a new symbol in this scope.
     *
     * @param symbol The symbol to define.
     */
    void define(Symbol symbol);

    /**
     * Resolves a symbol by name in this scope or recursively in enclosing scopes.
     *
     * @param name The name of the symbol.
     * @return The resolved symbol, or null if not found.
     */
    Symbol resolve(String name);

    /**
     * Resolves a symbol locally by name only in this scope.
     *
     * @param name The name of the symbol.
     * @return The resolved symbol, or null if not found.
     */
    Symbol resolveLocal(String name);

    /**
     * Returns the enclosing (parent) scope.
     *
     * @return The enclosing scope, or null if this is the global scope.
     */
    Scope getEnclosingScope();

    /**
     * Returns the name of this scope (useful for debugging).
     *
     * @return The scope name.
     */
    String getScopeName();

    /**
     * Returns all symbols defined in this scope.
     *
     * @return A collection of symbols.
     */
    Collection<Symbol> getSymbols();
}