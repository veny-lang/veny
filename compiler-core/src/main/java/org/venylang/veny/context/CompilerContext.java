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

import org.venylang.veny.semantic.SymbolTable;
import org.venylang.veny.util.ErrorReporter;

import java.nio.file.Path;

/**
 * Project-wide compiler context. Holds shared state across all files being compiled.
 */
public class CompilerContext {

    private final Path workingDirectory;
    private final ErrorReporter errorReporter;
    private final SymbolTable globalSymbols;

    public CompilerContext(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.errorReporter = new ErrorReporter();
        this.globalSymbols = new SymbolTable();
    }

    public Path workingDirectory() {
        return workingDirectory;
    }

    public ErrorReporter errorReporter() {
        return errorReporter;
    }

    public SymbolTable globalSymbols() {
        return globalSymbols;
    }

    public void addGlobalSymbols(SymbolTable symbols) {
        this.globalSymbols.merge(symbols); // Assumes merge is implemented
    }
}
