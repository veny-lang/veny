package org.venylang.veny.semantic.symbols;

/**
 * Represents the top-level global scope.
 * Holds built-in types and top-level class declarations.
 */
public class GlobalScope extends BaseScope {

    public GlobalScope() {
        super(null); // No enclosing scope
    }

    @Override
    public String getScopeName() {
        return "global";
    }
}
