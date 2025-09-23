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

package org.venylang.veny.runtime.core;

import org.venylang.veny.runtime.api.VenyObject;

/**
 * A boolean wrapper that implements the {@link VenyObject} interface.
 *
 * <p>Provides immutable, singleton instances for {@code true} and {@code false} values,
 * and offers basic logical operations.</p>
 */
public class VenyBool implements VenyObject {

    /**
     * Singleton instance representing the boolean value {@code true}.
     */
    public static final VenyBool TRUE = new VenyBool(true);

    /**
     * Singleton instance representing the boolean value {@code false}.
     */
    public static final VenyBool FALSE = new VenyBool(false);

    /**
     * The raw boolean value wrapped by this instance.
     */
    private final boolean value;

    /**
     * Private constructor to enforce use of singleton instances via {@link #of(boolean)}.
     *
     * @param value the boolean value to wrap
     */
    private VenyBool(boolean value) {
        this.value = value;
    }

    /**
     * Returns the singleton {@code VenyBool} instance for the given boolean value.
     *
     * @param value the boolean value
     * @return {@link #TRUE} if {@code value} is {@code true}, otherwise {@link #FALSE}
     */
    public static VenyBool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * Returns the raw boolean value.
     *
     * @return the primitive boolean value
     */
    public boolean raw() {
        return value;
    }

    /**
     * Returns the logical negation of this boolean.
     *
     * @return {@link #TRUE} if this is {@link #FALSE}, otherwise {@link #FALSE}
     */
    public VenyBool not() {
        return of(!value);
    }

    /**
     * Returns the logical AND of this boolean with another.
     *
     * @param other the other {@code VenyBool}
     * @return {@link #TRUE} if both values are true, otherwise {@link #FALSE}
     */
    public VenyBool and(VenyBool other) {
        return of(this.value && other.value);
    }

    /**
     * Returns the logical OR of this boolean with another.
     *
     * @param other the other {@code VenyBool}
     * @return {@link #TRUE} if either value is true, otherwise {@link #FALSE}
     */
    public VenyBool or(VenyBool other) {
        return of(this.value || other.value);
    }

    /**
     * Compares this boolean to another {@link VenyObject} for value equality.
     *
     * @param other the object to compare with
     * @return {@link #TRUE} if {@code other} is a {@code VenyBool} with the same value, otherwise {@link #FALSE}
     */
    public VenyBool equalsTo(VenyObject other) {
        if (!(other instanceof VenyBool b)) return FALSE;
        return of(this.value == b.value);
    }

    public VenyBool eq(VenyBool other) {
        return equalsTo(other);
    }

    public VenyBool neq(VenyBool other) {
        return of(!equalsTo(other).raw());
    }

    /**
     * Returns the textual representation of this boolean.
     *
     * @return {@link VenyText} with the value {@code "true"} or {@code "false"}
     */
    @Override
    public VenyText text() {
        return VenyText.of(value ? "true" : "false");
    }

    /**
     * Returns the type name of this object.
     *
     * @return {@link VenyText} containing {@code "Bool"}
     */
    @Override
    public VenyText typeName() {
        return VenyText.of("Bool");
    }
}
