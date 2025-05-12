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

import java.util.HashMap;
import java.util.Map;

/**
 * A utility to resolve types by name.
 */
public final class TypeResolver {

    private static final Map<String, BuiltinType> builtins = new HashMap<>();

    static {
        builtins.put("Int", BuiltinType.INT);
        builtins.put("Float", BuiltinType.FLOAT);
        builtins.put("String", BuiltinType.STRING);
        builtins.put("Bool", BuiltinType.BOOL);
        builtins.put("void", BuiltinType.VOID);
    }

    /**
     * Resolves a built-in type by name.
     *
     * @param name The type name as it appears in code.
     * @return The BuiltinType or null.
     */
    public static BuiltinType resolveBuiltin(String name) {
        return builtins.get(name);
    }
}
