package org.venylang.veny.semantic.symbols;

import org.venylang.veny.semantic.Scope;
import org.venylang.veny.semantic.SemanticException;
import org.venylang.veny.semantic.Symbol;

import java.util.*;

/**
 * A base class for scopes that provides symbol definition and resolution logic.
 * Suitable for block scopes, class scopes, method scopes, etc.
 */
public abstract class BaseScope implements Scope {

    protected final Scope enclosingScope;
    protected final Map<String, Symbol> symbols = new LinkedHashMap<>();

    /**
     * Constructs a scope with a reference to its enclosing (parent) scope.
     *
     * @param enclosingScope The scope that encloses this one, or null if top-level.
     */
    public BaseScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    /**
     * Adds a new symbol to this scope.
     *
     * @param symbol The symbol to define.
     */
    @Override
    public void define(Symbol symbol) {
        /*if (symbols.containsKey(symbol.getName())) {
            throw new SemanticException("Symbol '" + symbol.getName() + "' already declared in scope '" + getScopeName() + "'");
        }*/
        symbols.put(symbol.getName(), symbol);
    }

    /**
     * Resolves a symbol by name, searching this scope and recursively enclosing scopes.
     *
     * @param name The name of the symbol to resolve.
     * @return The symbol, or null if not found.
     */
    @Override
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        return enclosingScope != null ? enclosingScope.resolve(name) : null;
    }

    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }

    /**
     * Returns the enclosing (parent) scope.
     *
     * @return The parent scope, or null.
     */
    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    /**
     * Returns all symbols defined in this scope.
     *
     * @return A collection of symbols.
     */
    @Override
    public Collection<Symbol> getSymbols() {
        return Collections.unmodifiableCollection(symbols.values());
    }

    /**
     * Returns a string description of this scope for debugging.
     *
     * @return A descriptive string.
     */
    @Override
    public String toString() {
        return "Scope(" + getScopeName() + ")";
    }
}
