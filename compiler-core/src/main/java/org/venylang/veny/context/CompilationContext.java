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

import org.venylang.veny.symbols.SymbolTable;
import org.venylang.veny.parser.ast.AstNode;

/**
 * Represents per-file compiler state during semantic analysis and code generation.
 */
public class CompilationContext {

    private final ProjectContext projectContext;
    private final ParseContext parseContext;
    private final SymbolTable localSymbols;
    private AstNode ast;

    public CompilationContext(ProjectContext projectContext, ParseContext parseContext) {
        this.projectContext = projectContext;
        this.parseContext = parseContext;
        this.localSymbols = new SymbolTable(); // local (per-file) scope
    }

    public ProjectContext getProjectContext() {
        return projectContext;
    }

    public ParseContext getParseContext() {
        return parseContext;
    }

    public SymbolTable getLocalSymbols() {
        return localSymbols;
    }

    public AstNode getAST() {
        return ast;
    }

    public void setAST(AstNode ast) {
        this.ast = ast;
    }

    public void reportError(String message) {
        //TODO projectContext.errorReporter().report(parseContext.source(), message);
    }
}

