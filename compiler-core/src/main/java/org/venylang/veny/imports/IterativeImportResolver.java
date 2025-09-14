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

import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.symbols.GlobalScope;

import java.util.List;

public class IterativeImportResolver implements ImportResolver {

    /** The global scope where all types are already indexed. */
    private final GlobalScope globalScope;

    public IterativeImportResolver(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    /**
     * Resolve imports using the global symbol table.
     * Since CompilerPipeline.indexAllSources() already registered all types,
     * this step is just a validation pass.
     */
    @Override
    public void resolveImports(List<ImportRecord> imports) throws ImportResolutionException {
        for (ImportRecord imp : imports) {
            String fqcn = imp.packageName() + "." + imp.className();

            // Lookup type in global scope
            Symbol symbol = globalScope.resolve(fqcn);
            if (symbol == null) {
                throw new ImportResolutionException("Cannot resolve import: " + fqcn);
            }
        }
    }
}
