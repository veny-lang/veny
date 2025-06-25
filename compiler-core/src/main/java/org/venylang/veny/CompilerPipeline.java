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
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.Program;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.util.ParsedFile;
import org.venylang.veny.util.ParsedFileExtractor;
import org.venylang.veny.util.SourceFile;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompilerPipeline {

  private final CompilerContext compilerContext;

  public CompilerPipeline(CompilerContext compilerContext) {
    this.compilerContext = compilerContext;
  }

  public void compile(List<Path> filesToCompile) {
    SrcFileSet fileSet = new SrcFileSet();
    List<FileCompilationContext> fileCompilationContexts = parse(filesToCompile, fileSet);

    List<VenyFile> astNodes = fileCompilationContexts.stream().map(FileCompilationContext::getAST).toList();

    Program program = new Program(astNodes);
    System.out.println("AST: " + program);

    /** TODO SemanticAnalyzer analyzer =
        new SemanticAnalyzer(projectContext.globalSymbols(), projectContext.errorReporter());
    program.accept(analyzer);*/

    if (compilerContext.errorReporter().hasErrors()) {
      System.err.println("Compilation aborted due to semantic errors.");
      return;
    }

    String javaCode = JavaCodeGenerator.of(program).getCode();
    System.out.println("Generated Java code:");
    System.out.println(javaCode);
  }

  private List<FileCompilationContext> parse(List<Path> filesToCompile, SrcFileSet fileSet) {
    List<FileCompilationContext> contexts = new ArrayList<>();

    for (Path path : filesToCompile) {
      try {
        Optional<ParsedFile> parsedHeader = ParsedFileExtractor.of(path).extract();

        parsedHeader.ifPresentOrElse(
            parsedFile -> {
              validatePackagePath(path, parsedFile.packageName());

              try {
                String source = SourceFile.of(path).source();
                SrcFilePosMap fileMap = fileSet.addFile(path.toString(), source.length());

                ParseContext parseContext =
                    ParseContext.builder()
                        .source(source)
                        .filePath(path)
                        .srcFilePosMap(fileMap)
                        .build();

                VenyFile ast = parseSingleFile(source, parseContext);
                FileCompilationContext context = new FileCompilationContext(compilerContext, parseContext);
                context.setAST(ast);

                contexts.add(context);

              } catch (IOException ex) {
                compilerContext
                    .errorReporter();
                    //TODO .error("Error reading: " + path + " - " + ex.getMessage());
              }
            },
            () -> reportMissingPackage(path));

      } catch (IOException e) {
        compilerContext
            .errorReporter();
            //TODO .error("Failed to process file: " + path + " - " + e.getMessage());
      }
    }

    return contexts;
  }

  private VenyFile parseSingleFile(String source, ParseContext context) {
    List<Token> tokens = new Lexer(source, context.srcFilePosMap()).scanTokens();
    return new RecursiveDescentParser(tokens, context).parse();
  }

    private void validatePackagePath(Path filePath, String packageName) {
        Path relative = compilerContext.workingDirectory().relativize(filePath);
        String expectedDir = (relative.getParent() != null) ? relative.getParent().toString() : "";
        if (!expectedDir.replace(File.separatorChar, '.').equals(packageName)) {
            throw new CompilationException(relative + ": Package `" + packageName +
                    "` does not match file location `" + expectedDir + "`");
        }
    }

    private void reportMissingPackage(Path path) {
        System.err.println("Missing package declaration in file: " +
                compilerContext.workingDirectory().relativize(path));
    }

}

