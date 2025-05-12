package org.venylang.veny.semantic.types;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility to resolve types by name.
 */
public final class TypeResolver {

    private static final Map<String, BuiltinType> builtins = new HashMap<>();

    static {
        builtins.put("Int", BuiltinType.INT);
        builtins.put("Float", BuiltinType.FLOAT);
        builtins.put("String", BuiltinType.STRING);
        builtins.put("Bool", BuiltinType.BOOL);
        builtins.put("void", BuiltinType.VOID);
    }

    /**
     * Resolves a built-in type by name.
     *
     * @param name The type name as it appears in code.
     * @return The BuiltinType or null.
     */
    public static BuiltinType resolveBuiltin(String name) {
        return builtins.get(name);
    }
}
