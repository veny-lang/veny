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

package org.venylang.veny.semantic.symbols;

import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.Type;
import org.venylang.veny.util.Visibility;

/**
 * Represents fields, local variables, method parameters in Veny source code.
 */
public class VariableSymbol extends Symbol {

    private final boolean isParameter;
    private final boolean isMutable;

    /**
     * Constructs a new VariableSymbol.
     *
     * @param name        The variable name.
     * @param type        The type of the variable.
     * @param visibility  The visibility modifier.
     * @param isParameter Whether this is a function/method parameter.
     * @param isMutable   Whether the variable is declared as `var` (true) or `val` (false).
     */
    public VariableSymbol(String name, Type type, Visibility visibility, boolean isParameter, boolean isMutable) {
        super(name, type, visibility);
        this.isParameter = isParameter;
        this.isMutable = isMutable;
    }

    public boolean isParameter() {
        return isParameter;
    }

    public boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        return (isParameter ? "Param" : "Var") + "(" + name + ": " + type + ")";
    }
}
