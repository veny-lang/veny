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

package org.venylang.cli.command;

import org.venylang.veny.CompilerPipeline;
import org.venylang.veny.context.CompilerContext;
import org.venylang.veny.util.*;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * {@code BuildCommand} is a CLI command that compiles Veny source code.
 * <p>
 * This command is implemented using Picocli and expects a source directory as its argument.
 * It is intended to be used within the Veny CLI toolchain.
 * </p>
 */
@CommandLine.Command(name = "build", description = "Compile Veny code")
public class BuildCommand implements Runnable, CliCommand {

    /**
     * The source directory containing Veny code to compile.
     * <p>
     * Defaults to the current directory (".") if not specified.
     * </p>
     */
    @CommandLine.Parameters(index = "0", description = "Source directory", defaultValue = ".")
    private File sourceDir;

    @CommandLine.Option(names = {"--stdlib", "--stdlib-path"}, description = "Optional path to stdlib source files")
    private Path stdLibPath;

    /**
     * The entry point called by Picocli when the command is executed from the CLI.
     * This should trigger the compilation process.
     */
    @Override
    public void run() {
        execute();
    }

    @Override
    public void execute() {
        Path workingDir = Paths.get(sourceDir.getPath());
        CompilerContext compilerContext = new CompilerContext(workingDir);
        CompilerPipeline pipeline = new CompilerPipeline(compilerContext);

        // 1️⃣ Compile stdlib (from a known location)
        StdlibLoader loader = new StdlibLoader("veny", Optional.of(stdLibPath));
        pipeline.compile(loader, false);

        // 2️⃣ Compile user project
        SourceRoot userCode = new UserSourceRoot(workingDir);
        if (userCode.isEmpty()) {
            System.out.println("No .veny files found in: " + workingDir);
            return;
        }
        pipeline.compile(userCode, true);
    }

    /**
     * Returns the name of this command.
     *
     * @return the command name (e.g., "build")
     */
    @Override
    public String name() {
        return "build";
    }

    /**
     * Returns a short description of this command.
     *
     * @return a description of what the command does
     */
    @Override
    public String description() {
        return "Compile Veny code";
    }
}
