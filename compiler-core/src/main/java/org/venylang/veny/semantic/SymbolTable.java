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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a hierarchical symbol table used for both global and nested scopes.
 * The global scope is simply the root symbol table (i.e., has no parent).
 */
public class SymbolTable {

    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable() {
        this(null); // global scope
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Defines a symbol in the current scope.
     */
    public void define(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    /**
     * Resolves a symbol by name, walking up the scope chain if needed.
     */
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) return symbol;
        return (parent != null) ? parent.resolve(name) : null;
    }

    /**
     * Checks if a symbol is defined only in this scope.
     */
    public boolean isDefinedLocally(String name) {
        return symbols.containsKey(name);
    }

    /**
     * Merges symbols from another table into this one (e.g. from stdlib).
     */
    public void merge(SymbolTable other) {
        symbols.putAll(other.symbols);
    }

    /**
     * Returns true if this table has no parent (global scope).
     */
    public boolean isGlobalScope() {
        return parent == null;
    }

    public SymbolTable getParent() {
        return parent;
    }
}
