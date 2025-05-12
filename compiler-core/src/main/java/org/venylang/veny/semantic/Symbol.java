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

package org.venylang.veny.semantic;

import org.venylang.veny.util.Visibility;

/**
 * Represents a named symbol in the Lumina language.
 * This can be a variable, method, or class.
 */
public abstract class Symbol {
    protected final String name;
    protected Type type;
    protected final Visibility visibility;

    /**
     * Constructs a new symbol.
     *
     * @param name       The identifier of the symbol.
     * @param type       The type of the symbol.
     * @param visibility The visibility modifier (public or private).
     */
    public Symbol(String name, Type type, Visibility visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
    }

    public Symbol(String name, Visibility visibility) {
        this(name, null, visibility);
    }

    /**
     * Gets the name of the symbol.
     *
     * @return The symbol name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type associated with the symbol.
     *
     * @return The symbol type.
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets the visibility modifier of the symbol.
     *
     * @return The symbol visibility.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Returns whether this symbol is publicly accessible.
     *
     * @return True if public, false otherwise.
     */
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }

    /**
     * Returns whether this symbol is private.
     *
     * @return True if private, false otherwise.
     */
    public boolean isPrivate() {
        return visibility == Visibility.PRIVATE;
    }

    @Override
    public String toString() {
        return String.format("Symbol(name=%s, type=%s, visibility=%s)", name, type, visibility);
    }
}

