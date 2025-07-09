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
 * Represents the absence of a value in the Veny object model.
 * <p>
 * This class is used to indicate operations that return "nothing", similar to {@code void} in Java.
 * It is implemented as a singleton since all instances are functionally identical.
 * </p>
 */
public final class VenyVoid implements VenyObject {

    /**
     * The singleton instance of {@code VenyVoid}.
     */
    public static final VenyVoid INSTANCE = new VenyVoid();

    /**
     * Private constructor to enforce singleton usage.
     */
    private VenyVoid() {}

    /**
     * Returns the singleton instance of {@code VenyVoid}.
     *
     * @return the sole {@code VenyVoid} instance
     */
    public static VenyVoid get() {
        return INSTANCE;
    }

    /**
     * Returns a textual representation of this object.
     *
     * @return a {@link VenyText} containing the string {@code "void"}
     */
    @Override
    public VenyText text() {
        return VenyText.of("void");
    }

    /**
     * Compares this object to another {@link VenyObject} for identity.
     *
     * @param other the object to compare against
     * @return {@code VenyBool.TRUE} if {@code other} is an instance of {@code VenyVoid},
     *         otherwise {@code VenyBool.FALSE}
     */
    @Override
    public VenyBool equalsTo(VenyObject other) {
        return VenyBool.of(other instanceof VenyVoid);
    }

    /**
     * Returns the type name of this object.
     *
     * @return a {@link VenyText} containing the string {@code "Void"}
     */
    @Override
    public VenyText typeName() {
        return VenyText.of("Void");
    }

    /**
     * Returns a string representation of this object for debugging or logging.
     *
     * @return the string {@code "void"}
     */
    @Override
    public String toString() {
        return "void";
    }
}
