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

import org.venylang.veny.runtime.api.Console;
import org.venylang.veny.runtime.api.VenyObject;

/**
 * Implementation of the {@link Console} interface.
 *
 * <p>This class is implemented as a thread-safe singleton using the
 * initialization-on-demand holder idiom to provide global access to a single instance.</p>
 */
public final class ConsoleImpl implements Console {

    /**
     * Holder class for the singleton instance of {@code ConsoleImpl}.
     *
     * <p>Uses the Java class loader's guarantees of thread safety and lazy initialization.</p>
     */
    private static class Holder {
        /**
         * The singleton instance of {@code ConsoleImpl}.
         */
        private static final ConsoleImpl INSTANCE = new ConsoleImpl();
    }

    /**
     * Private constructor to prevent external instantiation.
     */
    private ConsoleImpl() {}

    /**
     * Returns the singleton instance of {@code ConsoleImpl}.
     *
     * @return the singleton {@code ConsoleImpl} instance
     */
    public static ConsoleImpl instance() {
        return Holder.INSTANCE;
    }

    /**
     * Prints a {@link VenyText} message to the console without a newline.
     *
     * <p>This method is synchronized on {@code System.out} to ensure thread-safe output.</p>
     *
     * @param msg the message to print
     */
    @Override
    public void print(VenyText msg) {
        synchronized (System.out) {
            System.out.print(msg.raw());
        }
    }

    /**
     * Prints a {@link VenyText} message to the console followed by a newline.
     *
     * <p>This method is synchronized on {@code System.out} to ensure thread-safe output.</p>
     *
     * @param msg the message to print
     */
    @Override
    public void println(VenyText msg) {
        synchronized (System.out) {
            System.out.println(msg.raw());
        }
    }

    /**
     * Returns a textual representation of this console instance.
     *
     * @return a {@link VenyText} with the value {@code "<Console>"}
     */
    @Override
    public VenyText text() {
        return VenyText.of("<Console>");
    }

    /**
     * Checks reference equality between this console and another {@link VenyObject}.
     *
     * @param other the other object to compare with
     * @return {@link VenyBool#TRUE} if the same instance, {@link VenyBool#FALSE} otherwise
     */
    @Override
    public VenyBool equalsTo(VenyObject other) {
        return VenyBool.of(this == other);
    }

    /**
     * Returns the type name of this object.
     *
     * @return a {@link VenyText} containing {@code "Console"}
     */
    @Override
    public VenyText typeName() {
        return VenyText.of("Console");
    }
}
