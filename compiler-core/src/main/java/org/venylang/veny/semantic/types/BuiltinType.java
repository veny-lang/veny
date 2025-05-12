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

/**
 * Represents a built-in type like Int, String, etc.
 */
public final class BuiltinType extends Type {

    public static final BuiltinType INT = new BuiltinType("Int");
    public static final BuiltinType FLOAT = new BuiltinType("Float");
    public static final BuiltinType STRING = new BuiltinType("String");
    public static final BuiltinType BOOL = new BuiltinType("Bool");
    public static final BuiltinType VOID = new BuiltinType("void");

    private final String name;

    private BuiltinType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        return this == other; // Strict: only assignable to same type
    }
}
