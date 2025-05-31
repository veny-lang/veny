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

package org.venylang.veny.semantic;

import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.Program;
import org.junit.jupiter.api.Test;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticAnalyzerTest {

    private Lexer makeLexer(ParseContext context) {
        return new Lexer(context.source(), context.srcFilePosMap());
    }

    private ParseContext makeParseContext(String source) {
        SrcFileSet fileSet = new SrcFileSet();
        List<Integer> lineOffsets = new ArrayList<>();
        lineOffsets.add(0);
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '\n') {
                lineOffsets.add(i + 1);
            }
        }
        int[] lines = lineOffsets.stream().mapToInt(Integer::intValue).toArray();
        SrcFilePosMap posMap = new SrcFilePosMap(fileSet, "test", 1, source.length(), lines);
        return ParseContext.builder()
                .source(source)
                .filePath(Path.of("test"))  // Can be adapted as needed
                .srcFilePosMap(posMap)
                .build();
    }

    private void analyze(String source) {
        ParseContext context = makeParseContext(source);
        Lexer lexer = makeLexer(context);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens, context);
        VenyFile parsedUnit = parser.parse();
        new SemanticAnalyzer().visitProgram(Program.of(parsedUnit));
    }

    @Test
    void duplicateClassNameShouldFail() {
        String source = """
            package org.venylang.veny.test
            
            class Foo {}
            class Foo {}
        """;
        assertThrows(SemanticException.class, () -> analyze(source));
    }

    @Test
    void duplicateVariableInSameScopeShouldFail() {
        String source = """
            package org.venylang.veny.test
            
            class Test {                
                var x: Int = 1
                var x: Int = 2
            }
        """;
        assertThrows(SemanticException.class, () -> analyze(source));
    }

    @Test
    void variableShadowingInInnerScopeShouldPass() {
        String source = """
            package org.venylang.veny.test
            
            class Test {
                var x: Int = 1
                shadow(): void {
                    var x: Int = 2
                }
            }
        """;
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    void uniqueClassNamesShouldPass() {
        String source = """
            package org.venylang.veny.test
            
            class Alpha {}
            class Beta {}
        """;
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    void duplicateMethodInClassShouldPassTemporarily() {
        // Overloading isn't handled yet, so allow for now unless restricted
        String source = """
            package org.venylang.veny.test
            
            class Service {
                foo(): void {}
                foo(): void {}
            }
        """;
        assertDoesNotThrow(() -> analyze(source)); // Adjust based on policy
    }
}