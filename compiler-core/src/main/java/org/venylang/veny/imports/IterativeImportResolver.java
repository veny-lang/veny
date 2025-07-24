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
import org.venylang.veny.parser.ast.ClassDecl;
import org.venylang.veny.parser.ast.InterfaceDecl;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.semantic.SemanticAnalyzer;
import org.venylang.veny.semantic.SemanticException;
import org.venylang.veny.semantic.symbols.ClassSymbol;
import org.venylang.veny.parser.ast.AstNode;
import org.venylang.veny.semantic.SymbolTable;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.io.IOException;
import java.nio.file.DirectoryStream;
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
public class IterativeImportResolver implements ImportResolver {

    /** The root directory where source files are located. */
    private final Path sourceRoot;

    /** The global symbol table used to store and resolve class definitions. */
    private final SymbolTable globalClassTable;

    /** Cache: fully–qualified class name → source file path. Filled lazily. */
    private final Map<String, Path> packageIndex = new HashMap<>();

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
            String fqcn = imp.packageName() + "." + imp.className();

            Path filePath = locateSourceFile(fqcn, imp.packageName());

            if (compiledFiles.contains(filePath)) {
                continue;
            }

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
     * Locates the source file for the given FQCN, populating the package index if necessary.
     *
     * @param fqcn         fully‑qualified class name
     * @param packageName  package portion of the FQCN
     * @return path to the source file
     * @throws ImportResolutionException if the file cannot be found
     */
    private Path locateSourceFile(String fqcn, String packageName) throws ImportResolutionException {
        Path path = packageIndex.get(fqcn);
        if (path != null) {
            return path;
        }

        Path packageDir = sourceRoot.resolve(packageName.replace('.', '/'));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(packageDir, "*.veny")) {
            for (Path p : stream) {
                AstNode ast = compileFileToAST(p);
                if (!(ast instanceof VenyFile venyFile)) continue;

                for (ClassDecl cls : venyFile.classes()) {
                    String declaredFqcn = venyFile.packageName() + "." + cls.name();
                    packageIndex.put(declaredFqcn, p);
                }
                for (InterfaceDecl iface : venyFile.interfaces()) {
                    String declaredFqcn = venyFile.packageName() + "." + iface.name();
                    packageIndex.put(declaredFqcn, p);
                }
            }
        } catch (IOException e) {
            throw new ImportResolutionException("Failed to scan package directory: " + packageDir);
        }

        path = packageIndex.get(fqcn);
        if (path == null) {
            throw new ImportResolutionException("Class not found: " + fqcn);
        }

        return path;
    }

    private String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
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
     * Performs semantic analysis on the AST and extracts the class symbol.
     *
     * @param ast the AST representing the class
     * @param imp the associated import record
     * @return the extracted class symbol
     */
    private ClassSymbol analyzeAndExtractSymbols(AstNode ast, ImportRecord imp) throws  ImportResolutionException {
        if (!(ast instanceof VenyFile venyFile)) {
            throw new IllegalArgumentException("Expected a VenyFile AST node");
        }

        // Step 1: Find the matching ClassDecl
        ClassDecl targetClass = null;
        for (ClassDecl cls : venyFile.classes()) {
            if (cls.name().equals(imp.className())) {
                targetClass = cls;
                break;
            }
        }

        if (targetClass == null) {
            throw new ImportResolutionException("Class " + imp.className() + " not found in file.");
        }

        // Step 2: Analyze the ClassDecl to extract symbols
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        // Manually visit just the class (not the whole file/program)
        targetClass.accept(analyzer);

        // Step 3: Get the class symbol from the global scope
        ClassSymbol symbol = analyzer.resolveClass(imp.className());

        if (symbol == null) {
            throw new SemanticException("Failed to extract symbol for class: " + imp.className());
        }

        return symbol;
    }
}
