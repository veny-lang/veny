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