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

package org.venylang.veny.semantic.types;

import org.venylang.veny.semantic.Type;
import org.venylang.veny.semantic.symbols.InterfaceSymbol;

/**
 * Represents a user-defined interface type.
 */
public class InterfaceType extends Type {

    private final InterfaceSymbol interfaceSymbol;

    public InterfaceType(InterfaceSymbol interfaceSymbol) {
        this.interfaceSymbol = interfaceSymbol;
    }

    public InterfaceSymbol getInterfaceSymbol() {
        return interfaceSymbol;
    }

    @Override
    public String getName() {
        return interfaceSymbol.getName();
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (other.equals(BuiltinType.NULL)) {
            return true; // null can be assigned to any reference type
        }

        // If other is an interface
        if (other instanceof InterfaceType otherInterface) {
            // Equal for now; extend later for interface inheritance
            return this.interfaceSymbol.equals(otherInterface.interfaceSymbol);
        }

        // If other is a class, check if the class implements this interface (future work)
        if (other instanceof ClassType otherClass) {
            // TODO: Add interface implementation checking logic later
            return false;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InterfaceType it &&
                it.interfaceSymbol.getName().equals(this.interfaceSymbol.getName());
    }

    @Override
    public int hashCode() {
        return interfaceSymbol.getName().hashCode();
    }

    @Override
    public String toString() {
        return "InterfaceType(" + interfaceSymbol.getName() + ")";
    }
}
