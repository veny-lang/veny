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

    public static void main(String[] args) {
        new Compiler().run(args);
    }

    public void run(String[] args) {
        Path workingDir;

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

        List<Path> lmFiles = collectLuminaFiles(workingDir);
        if (lmFiles.isEmpty()) {
            System.out.println("No .lm files found in: " + workingDir);
            return;
        }

        for (Path file : lmFiles) {
            System.out.println("Compiling: " + file);
            compileFile(file);
        }
    }

    private List<Path> collectLuminaFiles(Path dir) {
        List<Path> lmFiles = new ArrayList<>();
        try {
            Files.walk(dir)
                .filter(p -> p.toString().endsWith(".lm"))
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

                        // Later steps: semantic analysis, etc.

                    } catch (IOException ex) {
                        System.err.println("Error reading file during compilation: " + path);
                        ex.printStackTrace();
                    }
                },
                () -> System.err.println("Missing package declaration in file: " + path));
        } catch (IOException ex) {
            System.err.println("Could not read file: " + path);
            ex.printStackTrace();
        }
    }

    private void printUsage() {
        System.out.println("""
            Lumina Compiler (luminac)
            --------------------------
            Usage:
                luminac [<source-root-directory>]

            If no directory is given, current directory is used.

            Example:
                luminac src/
                luminac .
            """);
    }
}