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

/**
 * Resolves imports iteratively by traversing dependencies using a queue-based
 * breadth-first search strategy. It parses source files, extracts import statements,
 * analyzes class definitions, and registers them in a global symbol table.
 * <p>
 * This implementation also detects and reports circular import dependencies.
 */
public class IterativeImportResolver implements ImportResolver {

    /** The root directory where source files are located. */
    private final Path sourceRoot;

    /** The global symbol table used to store and resolve class definitions. */
    private final SymbolTable globalClassTable;

    /** Tracks files that have already been fully compiled and processed. */
    private final Set<Path> compiledFiles = new HashSet<>();

    /** Tracks files currently being compiled to detect circular imports. */
    private final Set<Path> compilingNow = new HashSet<>();

    /**
     * Constructs a new {@code IterativeImportResolver}.
     *
     * @param sourceRoot the root directory containing source files
     * @param globalClassTable the symbol table used for storing resolved class symbols
     */
    public IterativeImportResolver(Path sourceRoot, SymbolTable globalClassTable) {
        this.sourceRoot = sourceRoot;
        this.globalClassTable = globalClassTable;
    }

    /**
     * Resolves a list of root-level imports by traversing their dependencies iteratively.
     * <p>
     * Each import is compiled into an AST, nested imports are extracted recursively,
     * class symbols are defined, and circular references are detected and reported.
     *
     * @param rootImports the root-level imports to resolve
     * @throws ImportResolutionException if a circular import or duplicate class is detected,
     *                                   or if compilation fails
     */
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

            // Step 2: Extract nested imports
            List<ImportRecord> childImports = extractImports(ast);
            queue.addAll(childImports);

            // Step 3: Analyze class and extract symbol
            ClassSymbol classSym = analyzeAndExtractSymbols(ast, imp);

            // Step 4: Register class symbol
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

    /**
     * Resolves the file path of the imported class based on its package and class name.
     *
     * @param packageName the package of the class
     * @param className the name of the class
     * @return the path to the corresponding source file
     */
    private Path resolveImportPath(String packageName, String className) {
        return sourceRoot.resolve(Paths.get(packageName.replace('.', '/'), className + ".veny"));
    }

    /**
     * Compiles the file at the given path to an abstract syntax tree (AST).
     *
     * @param filePath the path to the source file
     * @return the parsed AST node
     * @throws ImportResolutionException if compilation fails
     */
    private AstNode compileFileToAST(Path filePath) throws ImportResolutionException {
        // TODO: Actual lexer → parser integration here
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Extracts import statements from the given AST.
     *
     * @param ast the abstract syntax tree of a source file
     * @return a list of import records found in the AST
     */
    private List<ImportRecord> extractImports(AstNode ast) {
        // TODO: Parse the AST to extract import declarations
        return List.of(); // Placeholder
    }

    /**
     * Performs semantic analysis on the AST and extracts the class symbol.
     *
     * @param ast the AST representing the class
     * @param imp the associated import record
     * @return the extracted class symbol
     */
    private ClassSymbol analyzeAndExtractSymbols(AstNode ast, ImportRecord imp) {
        // TODO: Semantic analysis and symbol extraction
        // return new ClassSymbol(imp.className()); // Placeholder
        return null; // TODO: Implement actual logic
    }
}
