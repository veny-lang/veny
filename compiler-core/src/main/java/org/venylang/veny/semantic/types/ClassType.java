package org.venylang.veny.semantic.types;

import org.venylang.veny.semantic.Type;
import org.venylang.veny.semantic.symbols.ClassSymbol;

import java.util.Objects;

/**
 * Represents a user-defined class type.
 */
public class ClassType extends Type {

    private final ClassSymbol classSymbol;

    public ClassType(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    @Override
    public String getName() {
        return classSymbol.getName();
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (other.equals(PrimitiveType.NULL)) {
            return true; // null can be assigned to any object/class type
        }

        if (!(other instanceof ClassType otherClass)) {
            return false;
        }

        return this.getClassSymbol().equals(otherClass.getClassSymbol());
        // For inheritance support later, check subclass relationships here.
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClassType ct &&
                ct.classSymbol.getName().equals(this.classSymbol.getName());
    }

    @Override
    public int hashCode() {
        return classSymbol.getName().hashCode();
    }
}