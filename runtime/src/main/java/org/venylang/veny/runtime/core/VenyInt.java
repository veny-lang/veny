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
 * Immutable wrapper around an {@code int} value that implements the {@link VenyObject} interface.
 *
 * <p>Supports basic arithmetic, comparison operations, and provides methods for object-based
 * functional interoperability in the Veny object model.</p>
 */
public final class VenyInt implements VenyObject {

    /**
     * The primitive integer value wrapped by this instance.
     */
    private final int value;

    /**
     * Constructs a new {@code VenyInt} instance with the specified value.
     *
     * @param value the integer value to wrap
     */
    public VenyInt(int value) {
        this.value = value;
    }

    /**
     * Factory method to create a new {@code VenyInt} instance.
     *
     * @param value the integer value
     * @return a new {@code VenyInt} wrapping the given value
     */
    public static VenyInt of(int value) {
        return new VenyInt(value);
    }

    // ─────────────────────────────
    // Core arithmetic operations
    // ─────────────────────────────

    /**
     * Returns a new {@code VenyInt} representing the sum of this and another {@code VenyInt}.
     *
     * @param other the value to add
     * @return result of addition
     */
    public VenyInt add(VenyInt other) {
        return new VenyInt(this.value + other.value);
    }

    /**
     * Returns a new {@code VenyInt} representing the difference between this and another {@code VenyInt}.
     *
     * @param other the value to subtract
     * @return result of subtraction
     */
    public VenyInt subtract(VenyInt other) {
        return new VenyInt(this.value - other.value);
    }

    /**
     * Alias for subtract
     *
     * @param other
     * @return
     */
    public VenyInt sub(VenyInt other) {
        return subtract(other);
    }

    /**
     * Returns a new {@code VenyInt} representing the product of this and another {@code VenyInt}.
     *
     * @param other the value to multiply
     * @return result of multiplication
     */
    public VenyInt multiply(VenyInt other) {
        return new VenyInt(this.value * other.value);
    }

    /**
     * Alias for multiply
     * @param other
     * @return
     */
    public VenyInt mul(VenyInt other) {
        return multiply(other);
    }

    /**
     * Returns a new {@code VenyInt} representing the quotient of this divided by another {@code VenyInt}.
     *
     * @param other the divisor
     * @return result of division
     * @throws ArithmeticException if division by zero occurs
     */
    public VenyInt divide(VenyInt other) {
        if (other.value == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new VenyInt(this.value / other.value);
    }

    /**
     * Alias for divide
     * @param other
     * @return
     */
    public VenyInt div(VenyInt other) {
        return divide(other);
    }

    /**
     * Returns a new {@code VenyInt} representing the remainder of division (modulo).
     *
     * @param other the divisor
     * @return remainder after division
     */
    public VenyInt modulo(VenyInt other) {
        return new VenyInt(this.value % other.value);
    }

    /**
     * Returns a new {@code VenyInt} representing the negation of this value.
     *
     * @return negated value
     */
    public VenyInt negate() {
        return new VenyInt(-this.value);
    }

    /**
     * Returns the absolute value of this integer.
     *
     * @return a new {@code VenyInt} with the absolute value
     */
    public VenyInt abs() {
        return new VenyInt(Math.abs(this.value));
    }

    /**
     * Returns the sign of this integer: -1 for negative, 0 for zero, 1 for positive.
     *
     * @return a new {@code VenyInt} representing the sign
     */
    public VenyInt sign() {
        return new VenyInt(Integer.compare(this.value, 0));
    }

    // ─────────────────────────────
    // Comparison operations
    // ─────────────────────────────

    /**
     * Returns whether this value is less than the given value.
     *
     * @param other the value to compare against
     * @return {@code VenyBool.TRUE} if less, otherwise {@code VenyBool.FALSE}
     */
    public VenyBool lessThan(VenyInt other) {
        return VenyBool.of(this.value < other.value);
    }

    /**
     * Returns whether this value is greater than the given value.
     *
     * @param other the value to compare against
     * @return {@code VenyBool.TRUE} if greater, otherwise {@code VenyBool.FALSE}
     */
    public VenyBool greaterThan(VenyInt other) {
        return VenyBool.of(this.value > other.value);
    }

    /**
     * Compares this value to another {@code VenyInt}.
     *
     * @param other the value to compare against
     * @return {@code VenyInt} wrapping -1, 0, or 1 depending on whether this is less than,
     *         equal to, or greater than {@code other}
     */
    public VenyInt compareTo(VenyInt other) {
        return new VenyInt(Integer.compare(this.value, other.value));
    }

    public VenyBool eq(VenyInt other) {
        return VenyBool.of(this.value == other.value);
    }

    public VenyBool neq(VenyInt other) {
        return VenyBool.of(this.value != other.value);
    }

    public VenyBool lt(VenyInt other) {
        return VenyBool.of(this.value < other.value);
    }

    public VenyBool lte(VenyInt other) {
        return VenyBool.of(this.value <= other.value);
    }

    public VenyBool gt(VenyInt other) {
        return VenyBool.of(this.value > other.value);
    }

    public VenyBool gte(VenyInt other) {
        return VenyBool.of(this.value >= other.value);
    }

    // ─────────────────────────────
    // Conversions and metadata
    // ─────────────────────────────

    /**
     * Returns the raw integer value.
     *
     * @return the primitive {@code int} value
     */
    public int raw() {
        return this.value;
    }

    /**
     * Returns a textual representation of this integer.
     *
     * @return a {@link VenyText} containing the string form of the value
     */
    @Override
    public VenyText text() {
        return new VenyText(Integer.toString(value));
    }

    /**
     * Compares this value to another {@link VenyObject} for equality.
     *
     * @param other the object to compare
     * @return {@code VenyBool.TRUE} if {@code other} is a {@code VenyInt} with the same value, otherwise {@code VenyBool.FALSE}
     */
    @Override
    public VenyBool equalsTo(VenyObject other) {
        if (!(other instanceof VenyInt i)) {
            return VenyBool.FALSE;
        }
        return VenyBool.of(this.value == i.value);
    }

    /**
     * Returns the type name of this object.
     *
     * @return a {@link VenyText} containing the integer as a string (possibly for debugging or display)
     */
    @Override
    public VenyText typeName() {
        return new VenyText(Integer.toString(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    // ─────────────────────────────
    // Future Enhancements
    // ─────────────────────────────

    /**
     * TODO: Converts this {@code VenyInt} to a {@code VenyFloat} representation.
     *
     * @return a {@code VenyFloat} with equivalent double value
     */
    // public VenyFloat toFloat() {
    //     return new VenyFloat((double) this.value);
    // }
}
