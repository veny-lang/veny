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

import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ParseException;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.AstNode;
import org.venylang.veny.parser.ast.ClassDecl;
import org.venylang.veny.parser.ast.InterfaceDecl;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.semantic.SemanticAnalyzer;
import org.venylang.veny.semantic.SemanticException;
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.symbols.GlobalScope;
import org.venylang.veny.util.SourceFile;
import org.venylang.veny.util.SourceRoot;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Resolves imports iteratively by traversing dependencies using a queue-based
 * breadth-first search strategy. It parses source files, extracts import statements,
 * analyzes class definitions, and registers them in a global symbol table.
 * <p>
 * This implementation also detects and reports circular import dependencies.
 */
public class IterativeImportResolver implements ImportResolver{

    /** The roots where source files are located (stdlib, user code, etc.). */
    private final List<SourceRoot> sourceRoots;

    /** The global scope where all class/interface symbols are registered. */
    private final GlobalScope globalScope;

    /** Cache: fully–qualified class name → source file path. */
    private final Map<String, Path> packageIndex = new HashMap<>();

    /** Tracks files already fully compiled. */
    private final Set<Path> compiledFiles = new HashSet<>();

    /** Tracks files currently being compiled (for circular import detection). */
    private final Set<Path> compilingNow = new HashSet<>();

    /**
     * Constructs a new {@code IterativeImportResolver}.
     *
     * @param sourceRoots available source files
     * @param globalScope the symbol table used for storing resolved class/interface symbols
     */
    public IterativeImportResolver(List<SourceRoot> sourceRoots, GlobalScope globalScope) {
        this.sourceRoots = sourceRoots;
        this.globalScope = globalScope;
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
            String fqcn = imp.packageName() + "." + imp.className();

            Path filePath = locateSourceFile(fqcn);

            if (compiledFiles.contains(filePath)) {
                continue;
            }
            if (compilingNow.contains(filePath)) {
                throw new CircularImportException("Circular import detected: " + fqcn);
            }

            compilingNow.add(filePath);

            // Step 1: Parse to AST
            AstNode ast = compileFileToAST(filePath);

            // Step 2: Extract nested imports
            List<ImportRecord> childImports = extractImports(ast);
            queue.addAll(childImports);

            // Step 3: Analyze and extract symbol (can be ClassSymbol or InterfaceSymbol)
            Symbol typeSym = analyzeAndExtractSymbols(ast, imp);

            // Step 4: Register symbol in global table
            if (globalScope.resolve(fqcn) != null) {
                throw new ImportResolutionException("Duplicate class: " + fqcn);
            }
            //typeSym.setName(fqcn); // Optional: ensure FQCN stored
            globalScope.define(typeSym);

            // Step 5: Mark as compiled
            compiledFiles.add(filePath);
            compilingNow.remove(filePath);
        }
    }

    /**
     * Locates the source file for the given fully-qualified class name.
     *
     * @param fqcn fully-qualified class name
     * @return path to the source file
     * @throws ImportResolutionException if not found
     */
    private Path locateSourceFile(String fqcn) throws ImportResolutionException {
        // 1. Cache lookup
        Path cached = packageIndex.get(fqcn);
        if (cached != null) {
            return cached;
        }

        // 2. Query each SourceRoot
        for (SourceRoot root : sourceRoots) {
            try {
                Optional<SourceFile> sourceFile = root.findSourceFile(fqcn);
                if (sourceFile.isPresent()) {
                    Path path = sourceFile.get().path();
                    packageIndex.put(fqcn, path);
                    return path;
                }
            } catch (IOException e) {
                throw new ImportResolutionException(
                        "Error while searching for " + fqcn + " in " + root.rootPath(), e
                );
            }
        }

        // 3. Fail if not found
        throw new ImportResolutionException("Class not found: " + fqcn);
    }

    /**
     * Performs semantic analysis on the AST and extracts the class symbol.
     *
     * @param ast the AST representing the class
     * @param imp the associated import record
     * @return the extracted class symbol
     */
    private Symbol analyzeAndExtractSymbols(AstNode ast, ImportRecord imp) throws  ImportResolutionException {
        if (!(ast instanceof VenyFile venyFile)) {
            throw new IllegalArgumentException("Expected a VenyFile AST node");
        }

        // Look for a matching class or interface
        AstNode targetDecl = null;

        // Find class first
        for (ClassDecl cls : venyFile.classes()) {
            if (cls.name().equals(imp.className())) {
                targetDecl = cls;
                break;
            }
        }

        // If not found, look for interface
        if (targetDecl == null) {
            for (InterfaceDecl iface : venyFile.interfaces()) {
                if (iface.name().equals(imp.className())) {
                    targetDecl = iface;
                    break;
                }
            }
        }

        if (targetDecl == null) {
            throw new ImportResolutionException("Type " + imp.className() + " not found in file.");
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer(globalScope);
        targetDecl.accept(analyzer);

        Symbol symbol = analyzer.resolveSymbol(imp.className());
        if (symbol == null) {
            throw new SemanticException("Failed to extract symbol for: " + imp.className());
        }

        return symbol;
    }

    /**
     * Extracts import statements from the given AST.
     *
     * @param ast the abstract syntax tree of a source file
     * @return a list of import records found in the AST
     */
    private List<ImportRecord> extractImports(AstNode ast) {
        if (!(ast instanceof VenyFile venyFile)) {
            throw new IllegalArgumentException("Expected VenyFile AST node");
        }

        return venyFile.imports().stream()
                .map(String::trim)
                .filter(fqcn -> fqcn.contains("."))
                .map(fqcn -> {
                    int dot = fqcn.lastIndexOf('.');
                    String pkg = fqcn.substring(0, dot);
                    String cls = fqcn.substring(dot + 1);
                    return new ImportRecord(pkg, cls);
                })
                .toList();
    }

    /**
     * Compiles the file at the given path to an abstract syntax tree (AST).
     *
     * @param filePath the path to the source file
     * @return the parsed AST node
     * @throws ImportResolutionException if compilation fails
     */
    private AstNode compileFileToAST(Path filePath) throws ImportResolutionException {
        try {
            String source = Files.readString(filePath);
            SrcFileSet fileSet = new SrcFileSet();
            SrcFilePosMap fileMap = fileSet.addFile(filePath.toString(), source.length()); // If needed, can be constructed differently

            ParseContext parseContext = ParseContext.builder()
                    .source(source)
                    .filePath(filePath)
                    .srcFilePosMap(fileMap)
                    .build();

            List<Token> tokens = new Lexer(source, fileMap).scanTokens();
            VenyFile ast = new RecursiveDescentParser(tokens, parseContext).parse();

            return ast;

        } catch (IOException e) {
            throw new ImportResolutionException("Failed to read file: " + filePath, e);
        } catch (ParseException e) {
            throw new ImportResolutionException("Failed to parse file: " + filePath, e);
        }
    }
}
