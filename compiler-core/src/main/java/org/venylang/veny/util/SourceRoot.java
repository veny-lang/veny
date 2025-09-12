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

package org.venylang.veny.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents a logical root of Veny source files, such as the standard library
 * or a user project root. A {@code SourceRoot} is responsible for locating and
 * providing access to source files within its scope.
 * <p>
 * Implementations may represent different kinds of roots:
 * <ul>
 *     <li><strong>Filesystem directory</strong> – e.g. a user project folder</li>
 *     <li><strong>Classpath resources</strong> – e.g. bundled stdlib inside a JAR</li>
 *     <li><strong>Development override</strong> – e.g. stdlib sources on the developer’s machine</li>
 * </ul>
 */
public interface SourceRoot {

    /**
     * Returns the underlying path representing the root.
     * <p>
     * For filesystem-based roots, this is the root directory.
     * For classpath or virtual roots, it may be a logical or synthetic path.
     *
     * @return the root path for this source root (never {@code null})
     */
    Path rootPath();

    /**
     * Loads all source files accessible from this root.
     * <p>
     * The returned list must contain only valid source files
     * (e.g. with the {@code .veny} extension).
     *
     * @return a list of loaded {@link SourceFile} objects
     * @throws IOException if file traversal or reading fails
     */
    List<SourceFile> loadSources() throws IOException;

    /**
     * Indicates whether this root is a development override
     * (e.g. pointing to source files on the developer’s machine instead of
     * bundled resources).
     * <p>
     * This is primarily used for logging, diagnostics, and
     * differentiating between deployed and development environments.
     *
     * @return {@code true} if this root is a development override, {@code false} otherwise
     */
    boolean isDevOverride();

    /**
     * Determines whether this source root contains any source files.
     * <p>
     * This default implementation checks if the {@link SourceRoot#rootPath()} is non-null and
     * delegates to {@link FileCollector#isEmpty()} to assess whether any valid source files
     * are present under the root.
     * <p>
     * Implementations may override this method to provide more efficient or context-specific
     * checks.
     *
     * @return {@code true} if the root path is null or no source files are found; {@code false} otherwise
     */
    default boolean isEmpty() {
        if (rootPath() == null) {
            return true;
        }
        return FileCollector.of(rootPath()).isEmpty();
    }
}
