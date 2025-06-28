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

import org.venylang.veny.semantic.symbols.ClassSymbol;
import org.venylang.veny.parser.ast.AstNode;
import org.venylang.veny.semantic.SymbolTable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class IterativeImportResolver implements ImportResolver {

    private final Path sourceRoot;
    private final SymbolTable globalClassTable;
    private final Set<Path> compiledFiles = new HashSet<>();
    private final Set<Path> compilingNow = new HashSet<>(); // For cycle detection

    public IterativeImportResolver(Path sourceRoot, SymbolTable globalClassTable) {
        this.sourceRoot = sourceRoot;
        this.globalClassTable = globalClassTable;
    }

    @Override
    public void resolveImports(List<ImportRecord> rootImports) throws ImportResolutionException {
        Queue<ImportRecord> queue = new ArrayDeque<>(rootImports);

        while (!queue.isEmpty()) {
            ImportRecord imp = queue.poll();

            Path filePath = resolveImportPath(imp.packageName(), imp.className());

            if (compiledFiles.contains(filePath)) continue;
            if (compilingNow.contains(filePath)) {
                throw new CircularImportException("Circular import detected: " + filePath);
            }

            compilingNow.add(filePath);

            // Step 1: Parse file to AST
            AstNode ast = compileFileToAST(filePath);

            // Step 2: Extract any nested imports
            List<ImportRecord> childImports = extractImports(ast);
            queue.addAll(childImports);

            // Step 3: Analyze and extract the class symbol from the AST
            ClassSymbol classSym = analyzeAndExtractSymbols(ast, imp); // assumes one class per file

            // Step 4: Register in the global symbol table
            String fullName = imp.packageName() + "." + imp.className();
            if (globalClassTable.resolve(fullName) != null) {
                throw new ImportResolutionException("Duplicate class: " + fullName);
            }

            globalClassTable.define(fullName, classSym);

            // Step 5: Mark file as compiled
            compiledFiles.add(filePath);
            compilingNow.remove(filePath);
        }
    }

    private Path resolveImportPath(String packageName, String className) {
        return sourceRoot.resolve(Paths.get(packageName.replace('.', '/'), className + ".veny"));
    }

    // Placeholder for your actual compiler implementation:
    private AstNode compileFileToAST(Path filePath) throws ImportResolutionException {
        // TODO: Actual lexer → parser integration here
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private List<ImportRecord> extractImports(AstNode ast) {
        // TODO: Parse the AST to extract import declarations
        return List.of(); // Placeholder
    }

    private ClassSymbol analyzeAndExtractSymbols(AstNode ast, ImportRecord imp) {
        // TODO: Semantic analysis and symbol extraction
        //return new ClassSymbol(imp.className()); // Placeholder
        //TODO
        return null;
    }
}

