package org.venylang.veny.semantic.types;

import org.venylang.veny.semantic.Type;

/**
 * Represents a built-in type like Int, String, etc.
 */
public final class BuiltinType extends Type {

    public static final BuiltinType INT = new BuiltinType("Int");
    public static final BuiltinType FLOAT = new BuiltinType("Float");
    public static final BuiltinType STRING = new BuiltinType("String");
    public static final BuiltinType BOOL = new BuiltinType("Bool");
    public static final BuiltinType VOID = new BuiltinType("void");

    private final String name;

    private BuiltinType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        return this == other; // Strict: only assignable to same type
    }
}
