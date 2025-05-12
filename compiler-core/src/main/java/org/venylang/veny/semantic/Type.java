package org.venylang.veny.semantic;

/**
 * Abstract base class for all types in Lumina.
 */
public abstract class Type {

    /**
     * Gets the name of the type (e.g., "Int", "Greeter").
     *
     * @return The type name.
     */
    public abstract String getName();

    /**
     * Checks whether this type can be assigned from another.
     *
     * @param other The source type.
     * @return True if assignable, false otherwise.
     */
    public abstract boolean isAssignableFrom(Type other);

    @Override
    public String toString() {
        return getName();
    }
}