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

package org.venylang.veny.imports;

import java.util.List;

/**
 * Defines the contract for resolving a list of {@link ImportRecord}s,
 * typically as part of an import resolution pipeline.
 * <p>
 * Implementations of this interface may perform tasks such as:
 * <ul>
 *     <li>Resolving transitive imports</li>
 *     <li>Detecting circular dependencies</li>
 *     <li>Validating or filtering imports</li>
 *     <li>Loading class metadata</li>
 * </ul>
 */
public interface ImportResolver {

    /**
     * Resolves the provided list of root-level {@link ImportRecord}s.
     * <p>
     * This method may modify the input list, load additional imports, or
     * throw an exception if resolution fails (e.g., due to circular references
     * or missing dependencies).
     *
     * @param rootImports the initial list of import records to resolve
     * @throws ImportResolutionException if an error occurs during resolution
     */
    void resolveImports(List<ImportRecord> rootImports) throws ImportResolutionException;
}
