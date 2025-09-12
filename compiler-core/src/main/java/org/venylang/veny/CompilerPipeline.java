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
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.Program;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.util.ParsedFile;
import org.venylang.veny.util.ParsedFileExtractor;
import org.venylang.veny.util.SourceFile;
import org.venylang.veny.util.SourceRoot;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ImportResolver  importResolver;

    /**
     * Constructs a new {@code CompilerPipeline} with the given compiler context.
     *
     * @param compilerContext the context containing configuration and shared resources
     */
    public CompilerPipeline(CompilerContext compilerContext) {
        this.compilerContext = compilerContext;
    this.importResolver = compilerContext.importResolver();
    }

    /**
     * Compiles all source files provided by the given {@link SourceRoot}.
     * <p>
     * This method loads source files from the specified root, parses them
     * into ASTs, performs semantic analysis, and optionally generates Java code.
     *
     * @param sourceRoot   the source root containing all files to compile
     * @param generateCode {@code true} to generate Java code after analysis; {@code false} to stop at analysis
     * @throws RuntimeException if loading source files fails
     */
    public void compile(SourceRoot sourceRoot, boolean generateCode) {
        try {
            List<SourceFile> sources = sourceRoot.loadSources();
            processCompilation(parse(sources, new SrcFileSet(), sourceRoot.rootPath()), generateCode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sources from " + sourceRoot.rootPath(), e);
        }
    }

    /**
     * Parses a collection of {@link SourceFile} objects into {@link FileCompilationContext} instances.
     * <p>
     * Each source file is tokenized, parsed into an AST, and wrapped in a
     * {@code FileCompilationContext} for further compilation stages.
     *
     * @param sources  the list of source files to parse
     * @param fileSet  a {@link SrcFileSet} to track file mappings and positions
     * @param rootPath the root directory for resolving relative paths
     * @return a list of {@code FileCompilationContext} objects representing parsed files
     * @throws RuntimeException if parsing any file fails
     */
    private List<FileCompilationContext> parse(List<SourceFile> sources, SrcFileSet fileSet, Path rootPath) {
        List<FileCompilationContext> contexts = new ArrayList<>();

        for (SourceFile sourceFile : sources) {
            Path path = sourceFile.path();
            try {
                Optional<ParsedFile> parsedHeader = ParsedFileExtractor.of(path).extract();
                parsedHeader.ifPresentOrElse(parsedFile -> {
                    validatePackagePath(path, parsedFile.packageName(), rootPath);
                    contexts.add(compileSingleFile(path, sourceFile.source(), fileSet));
                }, () -> reportMissingPackage(path, rootPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return contexts;
    }

    /**
     * Processes the given list of file compilation contexts to build an AST, resolve imports,
     * and optionally generate Java code.
     * <p>
     * This method performs several compilation phases:
     * <ul>
     *     <li>Extracts the ASTs from all provided {@link FileCompilationContext} objects</li>
     *     <li>Constructs a {@link Program} node representing the entire compilation unit</li>
     *     <li>Extracts and resolves import statements from the AST</li>
     *     <li>(Optionally) Performs code generation if no errors are reported</li>
     * </ul>
     * <p>
     * If semantic errors are present (reported via the {@link ErrorReporter} in the compiler context),
     * code generation is skipped.
     *
     * @param contexts     the list of {@link FileCompilationContext} instances representing parsed source files
     * @param generateCode whether Java code should be generated from the final AST
     */
    private void processCompilation(List<FileCompilationContext> contexts, boolean generateCode) {
        List<VenyFile> astNodes = contexts.stream()
                .map(FileCompilationContext::getAST)
                .toList();

        Program program = new Program(astNodes);
        System.out.println("AST: " + program);

        // 🔽 Extract import records and resolve them
        List<ImportRecord> imports = astNodes.stream()
                .flatMap(file -> {
                    try {
                        return ImportExtractor.of(file).extract().stream();
                    } catch (ImportResolutionException e) {
                        // TODO: Add error to error reporter
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        /** TODO: Uncomment and implement semantic analysis
         * SemanticAnalyzer analyzer =
         *     new SemanticAnalyzer(projectContext.globalSymbols(), projectContext.errorReporter());
         * program.accept(analyzer);
         */

        if (compilerContext.errorReporter().hasErrors()) {
            System.err.println("Compilation aborted due to semantic errors.");
            return;
        }

        if (generateCode) {
            String javaCode = JavaCodeGenerator.of(program).getCode();
            System.out.println("Generated Java code:");
            System.out.println(javaCode);
        }
    }

    /**
     * Compiles a single source file into a {@link FileCompilationContext}.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Registers the file with the {@link SrcFileSet} to track source positions</li>
     *     <li>Builds a {@link ParseContext} containing metadata and file content</li>
     *     <li>Parses the source into an abstract syntax tree ({@link VenyFile})</li>
     *     <li>Wraps the result in a {@link FileCompilationContext} for later compilation phases</li>
     * </ul>
     *
     * @param path     the path to the source file
     * @param source   the content of the source file as a string
     * @param fileSet  the file set used for tracking file positions
     * @return a {@link FileCompilationContext} representing the parsed source file
     */
    private FileCompilationContext compileSingleFile(Path path, String source, SrcFileSet fileSet) {
        SrcFilePosMap fileMap = fileSet.addFile(path.toString(), source.length());

        ParseContext parseContext = ParseContext.builder()
                .source(source)
                .filePath(path)
                .srcFilePosMap(fileMap)
                .build();

        VenyFile ast = parseSingleFile(source, parseContext);

        FileCompilationContext context = new FileCompilationContext(compilerContext, parseContext);
        context.setAST(ast);

        return context;
    }

    /**
     * Parses a single source file into an abstract syntax tree (AST).
     *
     * @param source  the source code as a string
     * @param context the parsing context
     * @return the parsed {@code VenyFile} AST
     */
    private VenyFile parseSingleFile(String source, ParseContext context) {
        List<Token> tokens = new Lexer(source, context.srcFilePosMap()).scanTokens();
        return new RecursiveDescentParser(tokens, context).parse();
    }

    /**
     * Validates that the file path matches the declared package name.
     *
     * @param filePath    the path to the source file
     * @param packageName the declared package name
     * @throws CompilationException if the package name does not match the file path
     */
    private void validatePackagePath(Path filePath, String packageName, Path rootDir) {
        //Path relative = compilerContext.workingDirectory().relativize(filePath);
        Path relative = rootDir.relativize(filePath);
        String expectedDir = (relative.getParent() != null) ? relative.getParent().toString() : "";
        if (!expectedDir.replace(File.separatorChar, '.').equals(packageName)) {
            throw new CompilationException(relative + ": Package `" + packageName +
                    "` does not match file location `" + expectedDir + "`");
        }
    }

    /**
     * Reports a missing package declaration for a source file.
     *
     * @param path     the path of the source file
     * @param rootPath the root directory relative to which the path is reported
     */
    private void reportMissingPackage(Path path, Path rootPath) {
        System.err.println("Missing package declaration in file: " + rootPath.relativize(path));
    }
}

