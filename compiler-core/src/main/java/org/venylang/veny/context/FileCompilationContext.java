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

import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.semantic.Scope;
import org.venylang.veny.semantic.symbols.BaseScope;

/**
 * Represents per-file compiler state during semantic analysis and code generation.
 */
public class FileCompilationContext {

    private final CompilerContext compilerContext;
    private final ParseContext parseContext;
    private final Scope fileScope;
    private VenyFile ast;

    public FileCompilationContext(CompilerContext compilerContext, ParseContext parseContext) {
        this.compilerContext = compilerContext;
        this.parseContext = parseContext;

        // Each file has its own scope chained to the global scope
        this.fileScope = new BaseScope(compilerContext.getGlobalScope()) {

            @Override
            public String getScopeName() {
                return "file:" + parseContext.filePath().toString();
            }
        };
    }

    public CompilerContext getProjectContext() {
        return compilerContext;
    }

    public ParseContext getParseContext() {
        return parseContext;
    }

    public Scope getFileScope() {
        return fileScope;
    }

    public VenyFile getAST() {
        return ast;
    }

    public void setAST(VenyFile ast) {
        this.ast = ast;
    }

    public void reportError(String message) {
        //TODO projectContext.errorReporter().report(parseContext.source(), message);
    }
}

