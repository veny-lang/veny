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
 * Immutable wrapper around a {@link String} that implements the {@link VenyObject} interface.
 *
 * <p>Provides text operations and identity-aware comparison in the Veny object model.</p>
 */
public final class VenyText implements VenyObject {

    /**
     * The raw string value wrapped by this instance.
     */
    private final String value;

    /**
     * Constructs a new {@code VenyText} instance with the specified string value.
     *
     * @param value the string to wrap
     */
    public VenyText(String value) {
        this.value = value;
    }

    /**
     * Factory method to create a new {@code VenyText} instance.
     *
     * @param value the string value
     * @return a new {@code VenyText} wrapping the given string
     */
    public static VenyText of(String value) {
        return new VenyText(value);
    }

    /**
     * Returns the raw string value.
     *
     * @return the primitive {@code String} value
     */
    public String raw() {
        return value;
    }

    // ─────────────────────────────
    // Operations
    // ─────────────────────────────

    /**
     * Returns a new {@code VenyText} representing the concatenation of this and another {@code VenyText}.
     *
     * @param other the {@code VenyText} to append
     * @return a new {@code VenyText} containing the combined string
     */
    public VenyText append(VenyText other) {
        return new VenyText(this.value + other.value);
    }

    public VenyText add(VenyText other) {
        return append(other);
    } // + operator

    public VenyBool eq(VenyText other) {
        return equalsTo(other);
    }

    public VenyBool neq(VenyText other) {
        return VenyBool.of(!equalsTo(other).raw());
    }

    /**
     * Compares this text to another {@link VenyObject} for string equality.
     *
     * @param other the object to compare
     * @return {@code VenyBool.TRUE} if {@code other} is a {@code VenyText} with the same content,
     *         otherwise {@code VenyBool.FALSE}
     */
    public VenyBool equalsTo(VenyObject other) {
        if (!(other instanceof VenyText text)) return VenyBool.FALSE;
        return VenyBool.of(this.value.equals(text.value));
    }

    /**
     * Returns this instance itself, as it's already a textual representation.
     *
     * @return this {@code VenyText} instance
     */
    @Override
    public VenyText text() {
        return this;
    }

    /**
     * Returns the type name of this object.
     *
     * @return a {@link VenyText} containing the string {@code "Text"}
     */
    @Override
    public VenyText typeName() {
        return VenyText.of("Text");
    }

    /**
     * Returns a quoted string representation of the text.
     *
     * @return the string value wrapped in double quotes
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
