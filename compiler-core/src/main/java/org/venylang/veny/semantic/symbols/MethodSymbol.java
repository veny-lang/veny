package org.venylang.veny.semantic.symbols;

import org.venylang.veny.semantic.Scope;
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.Type;
import org.venylang.veny.util.Visibility;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Represents a method declared within a class in Lumina.
 * Acts as a symbol and a scope for parameters and local variables.
 */
public class MethodSymbol extends Symbol implements Scope {

    private final Scope enclosingScope;
    private final Map<String, Symbol> parameters = new LinkedHashMap<>();
    private final Map<String, Symbol> locals = new LinkedHashMap<>();

    /**
     * Constructs a new MethodSymbol.
     *
     * @param name           The method name.
     * @param returnType     The return type of the method.
     * @param visibility     The visibility modifier.
     * @param enclosingScope The class or outer scope containing this method.
     */
    public MethodSymbol(String name, Type returnType, Visibility visibility, Scope enclosingScope) {
        super(name, returnType, visibility);
        this.enclosingScope = enclosingScope;
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
        if (symbol != null) return symbol;
        symbol = parameters.get(name);
        if (symbol != null) return symbol;
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
        return "MethodSymbol(" + name + ")";
    }
}
