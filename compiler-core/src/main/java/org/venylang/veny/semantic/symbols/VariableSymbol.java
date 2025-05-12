package org.venylang.veny.semantic.symbols;

import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.Type;
import org.venylang.veny.util.Visibility;

/**
 * Represents fields, local variables, method parameters in Lumina.
 */
public class VariableSymbol extends Symbol {

    private final boolean isParameter;
    private final boolean isMutable;

    /**
     * Constructs a new VariableSymbol.
     *
     * @param name        The variable name.
     * @param type        The type of the variable.
     * @param visibility  The visibility modifier.
     * @param isParameter Whether this is a function/method parameter.
     * @param isMutable   Whether the variable is declared as `var` (true) or `val` (false).
     */
    public VariableSymbol(String name, Type type, Visibility visibility, boolean isParameter, boolean isMutable) {
        super(name, type, visibility);
        this.isParameter = isParameter;
        this.isMutable = isMutable;
    }

    public boolean isParameter() {
        return isParameter;
    }

    public boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        return (isParameter ? "Param" : "Var") + "(" + name + ": " + type + ")";
    }
}
