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

import org.venylang.veny.context.CompilerContext;
import org.venylang.veny.util.FileCollector;

import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Compiler {
    private Path workingDir;

    public static void main(String[] args) {
        new Compiler().run(args);
    }

    public void run(String[] args) {
        if (args.length == 0) {
            workingDir = Paths.get(".").toAbsolutePath().normalize();
        } else if (args.length == 1) {
            workingDir = Paths.get(args[0]).toAbsolutePath().normalize();
        } else {
            System.out.println("Invalid arguments.");
            printUsage();
            return;
        }

        System.out.println("Working directory: " + workingDir);

        CompilerContext compilerContext = new CompilerContext(workingDir);
        CompilerPipeline pipeline = new CompilerPipeline(compilerContext);

        // 1️⃣ Compile stdlib (from a known location)
        //Path stdlibDir = Path.of("resources/stdlib"); // or wherever you keep stdlib files
        //compileStdlib(stdlibDir, pipeline);

        /*if (projectContext.errorReporter().hasErrors()) {
            System.err.println("Stdlib compilation failed. Aborting.");
            return;
        }*/

        // 2️⃣ Compile user project
        FileCollector collector = FileCollector.of(workingDir);
        if (collector.isEmpty()) {
            System.out.println("No .veny files found in: " + workingDir);
            return;
        }

        pipeline.compile(collector.stream().toList());
    }

    /** private void compile(List<Path> filesToCompile) {
        Program program = new Program(parseVenyFiles(filesToCompile));
        System.out.println("AST: " + program);

        // SEMANTIC ANALYSIS
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        program.accept(analyzer); // type resolution, scoping, etc.
        System.out.println("Semantic analysis completed.");

        // Code generation
        String javaCode = JavaCodeGenerator.of(program).getCode();
        System.out.println("Generated Java code:");
        System.out.println(javaCode);
    } */       //String javaCode = generator.visitProgram(program);


    /**
     * Parses the given list of Veny source files into a list of AST file nodes.
     *
     * @param filesToCompile paths to Veny source files
     * @return a list of parsed VenyFile AST nodes
     */
    /** public List<VenyFile> parseVenyFiles(List<Path> filesToCompile) {
        List<VenyFile> allFiles = new ArrayList<>();
        SrcFileSet fileSet = new SrcFileSet();

        filesToCompile.forEach(path -> {
            try {
                Optional<ParsedFile> result = ParsedFileExtractor.of(path).extract();
                result.ifPresentOrElse(parsedFile -> {
                    validatePackagePath(path, parsedFile.packageName());

                    System.out.println("Parsed: " + parsedFile);

                    try {
                        String source = SourceFile.of(path).source();

                        VenyFile venyFile = parseSingleFile(path, source, fileSet);
                        // Register file with the file set
                        /** SrcFilePosMap file = fileSet.addFile(path.toString(), source.length(), )
                        List<Token> tokens = new Lexer(source).scanTokens();
                        VenyFile venyFile = new RecursiveDescentParser(tokens).parse();*/
                        /** allFiles.add(venyFile);
                    } catch (IOException ex) {
                        System.err.println("Error reading file during compilation: " + workingDir.relativize(path));
                        ex.printStackTrace();
                    }
                },
                () -> reportMissingPackage(path));
            } catch (IOException e) {
                System.err.println("Failed to compile: " + path);
                e.printStackTrace();
            }
        });

        return allFiles;
    }*/

    /** private void validatePackagePath(Path filePath, String packageName) {
        Path relative = workingDir.relativize(filePath);
        String expectedDir = (relative.getParent() != null) ? relative.getParent().toString() : "";
        if (!expectedDir.replace(File.separatorChar, '.').equals(packageName)) {
            throw new CompilationException(relative + ": Package `" + packageName +
                    "` does not match file location `" + expectedDir + "`");
        }
    }

    private void reportMissingPackage(Path path) {
        System.err.println("Missing package declaration in file: " + workingDir.relativize(path));
    }

    private VenyFile parseSingleFile(Path path, String source, SrcFileSet fileSet) throws IOException {
        // Register the file
        SrcFilePosMap fileMap = fileSet.addFile(path.toString(), source.length());

        // Create parse context for this file
        ParseContext parseContext = ParseContext.builder()
                .source(source)
                .filePath(path)
                .srcFilePosMap(fileMap)
                .build();

        // Tokenize and parse
        List<Token> tokens = new Lexer(source, parseContext.srcFilePosMap()).scanTokens();
        return new RecursiveDescentParser(tokens, parseContext).parse();
    }*/

    private void compileStdlib(Path stdlibDir, CompilerPipeline pipeline) {
        System.out.println("Compiling standard library from: " + stdlibDir);
        FileCollector collector = FileCollector.of(stdlibDir);
        if (collector.isEmpty()) {
            System.err.println("No stdlib files found in: " + stdlibDir);
            return;
        }

        pipeline.compile(collector.stream().toList());
    }

    private void printUsage() {
        System.out.println("""
            Veny Compiler (venyc)
            --------------------------
            Usage:
                venyc [<source-root-directory>]

            If no directory is given, current directory is used.

            Example:
                venyc src/
                venyc .
            """);
    }
}