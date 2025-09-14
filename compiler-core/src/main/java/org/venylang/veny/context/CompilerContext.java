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

package org.venylang.veny.context;

import org.venylang.veny.imports.ImportResolver;
import org.venylang.veny.imports.IterativeImportResolver;
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.symbols.GlobalScope;
import org.venylang.veny.util.ErrorReporter;
import org.venylang.veny.util.SourceRoot;

import java.util.List;

/**
 * Project-wide compiler context. Holds shared state across all files being compiled.
 */
public class CompilerContext {

    private final List<SourceRoot> sourceRoots;
    private final ErrorReporter errorReporter;
    private final ImportResolver resolver;
    private final GlobalScope globalScope;

    public CompilerContext(List<SourceRoot> sourceRoots) {
        this.errorReporter = new ErrorReporter();
        this.globalScope = new GlobalScope();
        this.sourceRoots = sourceRoots;
        resolver  = new IterativeImportResolver(globalScope);
    }

    public ErrorReporter errorReporter() {
        return errorReporter;
    }

    public void addGlobalSymbol(Symbol symbol) {
        globalScope.define(symbol);
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    public ImportResolver importResolver() {
        return resolver;
    }

    public List<SourceRoot> sourceRoots() { return sourceRoots; }
}
