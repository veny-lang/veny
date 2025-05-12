package org.venylang.veny.semantic.types;

import org.venylang.veny.semantic.Type;

import java.util.Objects;

/**
 * Represents a primitive type (Int, Float, String, Bool, etc.).
 */
public class PrimitiveType extends Type {
    private final String name;

    public static final PrimitiveType INT = new PrimitiveType("Int");
    public static final PrimitiveType FLOAT = new PrimitiveType("Float");
    public static final PrimitiveType STRING = new PrimitiveType("String");
    public static final PrimitiveType BOOL = new PrimitiveType("Bool");
    public static final PrimitiveType VOID = new PrimitiveType("void");
    public static final PrimitiveType NULL = new PrimitiveType("null");

    private PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (this.equals(NULL)) {
            return other.equals(NULL);
        }
        if (other.equals(NULL)) {
            return false;
        }
        return this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PrimitiveType p) && p.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}