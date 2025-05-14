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
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.Program;
import org.venylang.veny.semantic.SemanticAnalyzer;
import org.venylang.veny.util.ParsedFile;
import org.venylang.veny.util.ParsedFileExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        List<Path> venyFiles = collectVenyFiles(workingDir);
        if (venyFiles.isEmpty()) {
            System.out.println("No .veny files found in: " + workingDir);
            return;
        }

        for (Path file : venyFiles) {
            System.out.println("Compiling: " + workingDir.relativize(file));
            compileFile(file);
        }
    }

    private List<Path> collectVenyFiles(Path dir) {
        List<Path> lmFiles = new ArrayList<>();
        try {
            Files.walk(dir)
                .filter(p -> p.toString().endsWith(".veny"))
                .forEach(lmFiles::add);
        } catch (IOException e) {
            System.err.println("Error walking directory: " + dir);
        }
        return lmFiles;
    }

    private void compileFile(Path path) {
        try {
            Optional<ParsedFile> result = ParsedFileExtractor.of(path).extract();
            result.ifPresentOrElse(parsedFile -> {
                if (!packageMatchesPath(path, parsedFile.packageName())) {
                    Path relative = workingDir.relativize(parsedFile.path());
                    String actualPath = (relative.getParent() != null)
                            ? relative.getParent().toString()
                            : "(working directory)";
                    throw new CompilationException(relative + ": Package: `" + parsedFile.packageName()
                            + "`, but file is in `" + actualPath + "`");
                }
                System.out.println("Parsed: " + parsedFile);

                try {
                    String source = Files.readString(path);
                    List<Token> tokens = new Lexer(source).scanTokens();

                    /*for (Token token : tokens) {
                        System.out.println(token);
                    }*/

                    RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
                    Program program = parser.parse(); // AST root
                    System.out.println("AST: " + program);

                    // SEMANTIC ANALYSIS
                    SemanticAnalyzer analyzer = new SemanticAnalyzer();
                    program.accept(analyzer); // type resolution, scoping, etc.
                    System.out.println("Semantic analysis completed.");

                    // Code generation
                    JavaCodeGenerator generator = new JavaCodeGenerator();
                    String javaCode = generator.visitProgram(program);
                    System.out.println("Generated Java code:");
                    System.out.println(javaCode);

                } catch (IOException ex) {
                    System.err.println("Error reading file during compilation: " + workingDir.relativize(path));
                    ex.printStackTrace();
                }
            },
            () -> System.err.println("Missing package declaration in file: " + workingDir.relativize(path)));
        } catch (IOException ex) {
            System.err.println("Could not read file: " + workingDir.relativize(path));
            ex.printStackTrace();
        }
    }

    private boolean packageMatchesPath(Path filePath, String packageName) {
        // Example: filePath = src/app/core/engine.veny
        //          packageName = app.core

        // Normalize: ["app", "core"]
        List<String> packageParts = List.of(packageName.split("\\."));

        // Normalize the file path, extract the last N parts (before the file name)
        Path parent = workingDir.relativize(filePath).getParent();
        if (parent == null) return false;

        List<String> pathParts = new ArrayList<>();
        for (Path part : parent) {
            pathParts.add(part.toString());
        }

        // Compare last N path parts with package parts
        int offset = pathParts.size() - packageParts.size();
        if (offset < 0) return false;

        for (int i = 0; i < packageParts.size(); i++) {
            if (!packageParts.get(i).equals(pathParts.get(offset + i))) {
                return false;
            }
        }
        return true;
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