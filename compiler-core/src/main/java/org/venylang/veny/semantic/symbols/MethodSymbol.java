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
import org.venylang.veny.semantic.Type;
import org.venylang.veny.semantic.types.CallableType;
import org.venylang.veny.util.Visibility;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Represents a method declared within a class in Veny.
 * Acts as a symbol and a scope for parameters and local variables.
 */
public class MethodSymbol extends Symbol implements Scope {

    private final Scope enclosingScope;
    private final Map<String, Symbol> parameters = new LinkedHashMap<>();
    private final Map<String, Symbol> locals = new LinkedHashMap<>();
    private final Type returnType;
    //private final CallableType type;

    /**
     * Constructs a new MethodSymbol.
     *
     * @param name           The method name.
     * @param returnType     The return type of the method.
     * @param visibility     The visibility modifier.
     * @param enclosingScope The class or outer scope containing this method.
     */
    public MethodSymbol(String name, Type returnType, Visibility visibility, Scope enclosingScope) {
        super(name, visibility);
        this.returnType = returnType;
        this.enclosingScope = enclosingScope;
    }

    public Type returnType() {
        return returnType;
    }

    @Override
    public void define(Symbol symbol) {
        if (symbol instanceof VariableSymbol var && var.isParameter()) {
            parameters.put(symbol.getName(), symbol);
        } else {
            locals.put(symbol.getName(), symbol);
        }
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = locals.get(name);
        if (symbol != null) {
            return symbol;
        }
        symbol = parameters.get(name);
        if (symbol != null) {
            return symbol;
        }
        return enclosingScope != null ? enclosingScope.resolve(name) : null;
    }

    public Symbol resolveLocal(String name) {
        return locals.get(name);
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return name;
    }

    @Override
    public Collection<Symbol> getSymbols() {
        Map<String, Symbol> combined = new LinkedHashMap<>(parameters);
        combined.putAll(locals);
        return combined.values();
    }

    public Collection<Symbol> getParameters() {
        return parameters.values();
    }

    public Collection<Symbol> getLocals() {
        return locals.values();
    }

    @Override
    public String toString() {
        return "MethodSymbol(" + name + " : " + returnType + ")";
    }
}
