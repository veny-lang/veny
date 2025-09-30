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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CallableType extends Type {
    private final List<Type> paramTypes;
    private final Type returnType;

    public CallableType(List<Type> paramTypes, Type returnType) {
        this.paramTypes = List.copyOf(paramTypes);
        this.returnType = returnType;
    }

    public List<Type> paramTypes() {
        return paramTypes;
    }

    public Type returnType() {
        return returnType;
    }

    @Override
    public String getName() {
        return "(" + paramTypes.stream()
                .map(Type::getName)
                .collect(Collectors.joining(", "))
                + ") → " + returnType.getName();
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (!(other instanceof CallableType ct)) return false;
        if (ct.paramTypes.size() != this.paramTypes.size()) return false;

        for (int i = 0; i < paramTypes.size(); i++) {
            if (!this.paramTypes.get(i).isAssignableFrom(ct.paramTypes.get(i))) {
                return false;
            }
        }
        return this.returnType.isAssignableFrom(ct.returnType);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CallableType ct)) return false;
        return paramTypes.equals(ct.paramTypes) && returnType.equals(ct.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramTypes, returnType);
    }
}