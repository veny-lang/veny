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
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.types.ClassType;
import org.venylang.veny.util.Visibility;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a class declaration in Lumina.
 * Stores its name, members (fields and methods), and its own scope.
 */
public class ClassSymbol extends Symbol implements Scope {

    private final Scope enclosingScope;
    private final Map<String, Symbol> members = new LinkedHashMap<>();

    /**
     * Constructs a new ClassSymbol.
     *
     * @param name           The name of the class.
     * @param enclosingScope The scope in which this class is defined (usually global).
     */
    public ClassSymbol(String name, Scope enclosingScope) {
        super(name, Visibility.PUBLIC);
        this.enclosingScope = enclosingScope;
        this.type = new ClassType(this);
    }

    /**
     * Defines a new member (field or method) in the class.
     *
     * @param symbol The member to define.
     */
    @Override
    public void define(Symbol symbol) {
        members.put(symbol.getName(), symbol);
    }

    /**
     * Looks up a member by name within the class.
     *
     * @param name The member name.
     * @return The symbol if found, null otherwise.
     */
    @Override
    public Symbol resolve(String name) {
        return members.get(name);
    }

    @Override
    public Symbol resolveLocal(String name) {
        return members.get(name);
    }

    /**
     * Returns the enclosing scope (usually the global scope).
     *
     * @return The enclosing scope.
     */
    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    /**
     * Returns the scope name, which is the class name.
     *
     * @return The class name.
     */
    @Override
    public String getScopeName() {
        return name;
    }

    /**
     * Returns all members (fields and methods) defined in the class.
     *
     * @return A map of member symbols.
     */
    public Map<String, Symbol> getMembers() {
        return members;
    }

    public List<Symbol> getMethods() {
        return members.values().stream()
            .filter(s -> s instanceof MethodSymbol)
            .toList();
    }

    public List<Symbol> getFields() {
        return members.values().stream()
            .filter(s -> s instanceof VariableSymbol)
            .toList();
    }

    public MethodSymbol getMethod(String name) {
        Symbol s = members.get(name);
        return (s instanceof MethodSymbol ms) ? ms : null;
    }

    public VariableSymbol getField(String name) {
        Symbol s = members.get(name);
        return (s instanceof VariableSymbol vs) ? vs : null;
    }

    /**
     * Returns the corresponding ClassType for type-checking purposes.
     *
     * @return A ClassType wrapping this symbol.
     */
    public ClassType asType() {
        return new ClassType(this);
    }

    @Override
    public Collection<Symbol> getSymbols() {
        return members.values();
    }

    @Override
    public String toString() {
        return "ClassSymbol(" + name + ")";
    }
}