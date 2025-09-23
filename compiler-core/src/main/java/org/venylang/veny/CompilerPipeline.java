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

package org.venylang.veny;

import org.venylang.veny.codegen.JavaCodeGenerator;
import org.venylang.veny.context.FileCompilationContext;
import org.venylang.veny.context.ParseContext;
import org.venylang.veny.context.CompilerContext;
import org.venylang.veny.imports.ImportExtractor;
import org.venylang.veny.imports.ImportRecord;
import org.venylang.veny.imports.ImportResolutionException;
import org.venylang.veny.imports.ImportResolver;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ParseException;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.ClassDecl;
import org.venylang.veny.parser.ast.InterfaceDecl;
import org.venylang.veny.parser.ast.Program;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.semantic.SemanticAnalyzer;
import org.venylang.veny.semantic.SemanticException;
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.symbols.ClassSymbol;
import org.venylang.veny.semantic.symbols.GlobalScope;
import org.venylang.veny.semantic.symbols.InterfaceSymbol;
import org.venylang.veny.util.SourceFile;
import org.venylang.veny.util.SourceRoot;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code CompilerPipeline} class represents the main compilation pipeline
 * for processing Veny source files. It orchestrates parsing, AST construction,
 * semantic analysis, and (optionally) Java code generation.
 * <p>
 * This class serves as the core entry point for compilation, providing
 * methods for compiling all source files from a {@link SourceRoot} or
 * individual source files.
 */
public class CompilerPipeline {

    private final CompilerContext compilerContext;
    private final ImportResolver importResolver;

    // 🔒 Atomic flag to ensure indexing is only done once
    private final AtomicBoolean indexed = new AtomicBoolean(false);

    /** Cache for already-parsed ASTs (avoids reparsing files). */
    private final Map<Path, VenyFile> astCache = new HashMap<>();

    public CompilerPipeline(CompilerContext compilerContext) {
        this.compilerContext = compilerContext;
        this.importResolver = compilerContext.importResolver();
    }

    /**
     * Phase 2: Compile all sources in a given root.
     * Builds ASTs (from cache), resolves imports, analyzes, and optionally generates code.
     */
    public void compile(SourceRoot sourceRoot, boolean generateCode) {
        // ✅ Ensure indexing happens only once
        ensureIndexed();

        try {
            List<SourceFile> sources = sourceRoot.loadSources();
            List<FileCompilationContext> contexts = parseFiles(sources);

            processCompilation(contexts, generateCode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sources from " + sourceRoot.rootPath(), e);
        }
    }

    /**
     * Phase 1: Index all classes/interfaces in all source roots.
     * Parses each file *once* and registers symbols in the global scope.
     */
    private void indexAllSources() {
        for (SourceRoot root : compilerContext.sourceRoots()) {
            try {
                for (SourceFile sourceFile : root.loadSources()) {
                    indexFile(sourceFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to index sources from " + root.rootPath(), e);
            }
        }
    }

    /**
     * Index a single file by parsing and registering all top-level declarations.
     */
    private void indexFile(SourceFile sourceFile) {
        Path path = sourceFile.path();
        String source = sourceFile.source();

        try {
            VenyFile ast = parseAndCache(path, source);

            String pkg = ast.packageName() == null ? "" : ast.packageName();
            GlobalScope globalScope = compilerContext.getGlobalScope();

            for (ClassDecl cls : ast.classes()) {
                String fqcn = pkg.isEmpty() ? cls.name() : pkg + "." + cls.name();
                defineSymbol(globalScope, fqcn, new ClassSymbol(fqcn, globalScope));
            }

            for (InterfaceDecl iface : ast.interfaces()) {
                String fqcn = pkg.isEmpty() ? iface.name() : pkg + "." + iface.name();
                defineSymbol(globalScope, fqcn, new InterfaceSymbol(fqcn, globalScope));
            }

        } catch (ParseException e) {
            System.err.println("Skipping file due to parse error: " + path);
        }
    }

    /**
     * Parse all source files into compilation contexts.
     * Reuses cached ASTs from the indexing pass.
     */
    private List<FileCompilationContext> parseFiles(List<SourceFile> sources) {
        List<FileCompilationContext> contexts = new ArrayList<>();
        SrcFileSet fileSet = new SrcFileSet();

        for (SourceFile sourceFile : sources) {
            Path path = sourceFile.path();
            String source = sourceFile.source();
            SrcFilePosMap fileMap = fileSet.addFile(path.toString(), source.length());

            ParseContext parseContext = ParseContext.builder()
                    .source(source)
                    .filePath(path)
                    .srcFilePosMap(fileMap)
                    .build();

            VenyFile ast = astCache.getOrDefault(path, parseSingleFile(source, parseContext));

            FileCompilationContext context = new FileCompilationContext(compilerContext, parseContext);
            context.setAST(ast);
            contexts.add(context);
        }

        return contexts;
    }

    /**
     * Process parsed files: resolve imports, analyze, and optionally generate code.
     */
    private void processCompilation(List<FileCompilationContext> contexts, boolean generateCode) {
        List<VenyFile> astNodes = contexts.stream()
                .map(FileCompilationContext::getAST)
                .toList();

        Program program = new Program(astNodes);

        List<ImportRecord> imports = astNodes.stream()
                .flatMap(file -> {
                    try {
                        return ImportExtractor.of(file).extract().stream();
                    } catch (ImportResolutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        try {
            importResolver.resolveImports(imports);
        } catch (ImportResolutionException e) {
            throw new RuntimeException("Failed to resolve imports", e);
        }

        // 2️⃣ Run semantic analysis
        SemanticAnalyzer analyzer =
                new SemanticAnalyzer(compilerContext.getGlobalScope(), compilerContext.errorReporter());
        program.accept(analyzer);

        if (compilerContext.errorReporter().hasErrors()) {
            System.err.println("Compilation aborted due to errors.");
            return;
        }

        if (generateCode) {
            Path sourcePath = compilerContext.getUserSourceRoot().rootPath();
            Path genDir = sourcePath.resolve("generated");

            // Ensure generated root exists
            try {
                Files.createDirectories(genDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create generated directory: " + genDir, e);
            }

            // 1) Per-class (and per-interface) files
            for (VenyFile venyFile : astNodes) {
                String packageName = venyFile.packageName() == null ? "" : venyFile.packageName();

                // Helper: compute package directory under generated/
                Path packageDir = packageName.isEmpty()
                        ? genDir
                        : genDir.resolve(packageName.replace('.', File.separatorChar));
                try {
                    Files.createDirectories(packageDir);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create package directory: " + packageDir, e);
                }

                // Emit one .java per class
                for (ClassDecl cls : venyFile.classes()) {
                    // Create a VenyFile that contains only this class (preserve user imports)
                    VenyFile single = new VenyFile(packageName, venyFile.imports(), List.of(cls), List.of());

                    String javaCode;
                    try {
                        javaCode = JavaCodeGenerator.of(new Program(List.of(single))).getCode();
                    } catch (Exception ex) {
                        System.err.println("Code generation failed for class " + cls.name() + ": " + ex.getMessage());
                        ex.printStackTrace();
                        continue;
                    }

                    Path outFile = packageDir.resolve(cls.name() + ".java");
                    try {
                        Files.writeString(outFile, javaCode, StandardCharsets.UTF_8);
                        //DEBUG System.out.println("WROTE: " + outFile.toAbsolutePath());
                    } catch (IOException ex) {
                        System.err.println("Failed to write file " + outFile + ": " + ex.getMessage());
                    }
                }

                // Optionally: emit one .java per interface as well
                for (InterfaceDecl iface : venyFile.interfaces()) {
                    VenyFile singleIface = new VenyFile(packageName, venyFile.imports(), List.of(), List.of(iface));
                    String javaCode;
                    try {
                        javaCode = JavaCodeGenerator.of(new Program(List.of(singleIface))).getCode();
                    } catch (Exception ex) {
                        System.err.println("Code generation failed for interface " + iface.name() + ": " + ex.getMessage());
                        ex.printStackTrace();
                        continue;
                    }

                    Path outFile = packageDir.resolve(iface.name() + ".java");
                    try {
                        Files.writeString(outFile, javaCode, StandardCharsets.UTF_8);
                        //DEBUG System.out.println("WROTE: " + outFile.toAbsolutePath());
                    } catch (IOException ex) {
                        System.err.println("Failed to write file " + outFile + ": " + ex.getMessage());
                    }
                }
            }

            // 2) Also print the big "all-in-one" program to console for debugging
            try {
                String allJava = JavaCodeGenerator.of(program).getCode();
                System.out.println("==================================================");
                System.out.println("Full Generated Java code (all files combined):");
                System.out.println("==================================================");
                System.out.println(allJava);
            } catch (Exception ex) {
                System.err.println("Failed to generate combined program Java for console output: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Parse a file into an AST and cache it.
     */
    private VenyFile parseAndCache(Path path, String source) throws ParseException {
        SrcFileSet fileSet = new SrcFileSet();
        SrcFilePosMap fileMap = fileSet.addFile(path.toString(), source.length());

        ParseContext parseContext = ParseContext.builder()
                .source(source)
                .filePath(path)
                .srcFilePosMap(fileMap)
                .build();

        VenyFile ast = parseSingleFile(source, parseContext);
        astCache.put(path, ast);
        return ast;
    }

    private VenyFile parseSingleFile(String source, ParseContext context) {
        List<Token> tokens = new Lexer(source, context.srcFilePosMap()).scanTokens();
        return new RecursiveDescentParser(tokens, context).parse();
    }

    private void defineSymbol(GlobalScope globalScope, String fqcn, Symbol symbol) {
        if (globalScope.resolveLocal(fqcn) != null) {
            throw new SemanticException("Duplicate symbol: " + fqcn);
        }
        globalScope.define(symbol);
    }

    /**
     * Ensure all sources are indexed exactly once.
     */
    private void ensureIndexed() {
        if (indexed.compareAndSet(false, true)) {
            // Only one thread will run this
            indexAllSources();
        }
    }
}

