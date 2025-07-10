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
import org.venylang.veny.imports.ImportResolver;
import org.venylang.veny.imports.IterativeImportResolver;
import org.venylang.veny.util.FileCollector;
import org.venylang.veny.util.SourceFile;
import org.venylang.veny.util.StdlibLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
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

        CompilerContext compilerContext = new CompilerContext(workingDir);
        ImportResolver resolver = new IterativeImportResolver(
                compilerContext.workingDirectory(), compilerContext.globalSymbols());
        CompilerPipeline pipeline = new CompilerPipeline(compilerContext, resolver);

        // 1️⃣ Compile stdlib (from a known location)
        StdlibLoader loader = new StdlibLoader("veny", Optional.empty());
        List<SourceFile> files = loader.load();

        //Path stdlibDir = Paths.get("resources/veny"); // or wherever you keep stdlib files
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