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

package org.venylang.veny.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ast.VenyFile;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parameterized test harness for {@link RecursiveDescentParser}.
 * <p>
 * This suite covers:
 * <ul>
 *   <li>Mandatory package handling.</li>
 *   <li>Import parsing.</li>
 *   <li>Class parsing (fields, methods).</li>
 *   <li>Interface parsing.</li>
 *   <li>Control flow (if/else).</li>
 *   <li>Expressions &amp; compound assignment.</li>
 *   <li>Special validation for <code>entry</code> method.</li>
 * </ul>
 */
class RecursiveDescentParserParameterizedTest {

    /**
     * Helper to lex + parse source into a VenyFile.
     */
    private VenyFile parse(String source) {
        SrcFilePosMap posMap = new SrcFilePosMap(
                new SrcFileSet(),
                "test",
                1,
                source.length(),
                new int[]{0}
        );

        Lexer lexer = new Lexer(source, posMap);
        List<Token> tokens = lexer.scanTokens();

        ParseContext ctx = ParseContext.builder()
                .source(source)
                .filePath(Path.of("test.veny"))
                .srcFilePosMap(posMap)
                .build();

        return new RecursiveDescentParser(tokens, ctx).parse();
    }

    /**
     * Provides valid code snippets that should parse successfully.
     */
    static Stream<Named<String>> validPrograms() {
        return Stream.of(
                Named.of("Minimal package + class", """
                package demo
                class App {
                    entry(args: [Text]): void {
                        Console.print("Hello")
                    }
                }
                """),
                Named.of("Class with field + method", """
                package demo
                import lib.Console
                class App {
                    var counter: Int
                    pub main(args: [Text]): void {
                        var x: Int = 42
                        Console.print(x)
                    }
                }
                """),
                Named.of("Interface with method", """
                package demo
                interface Greeter {
                    greet(name: Text): void
                }
                """),
                Named.of("If/else in method", """
                package demo
                class App {
                    entry(args: [Text]): void {
                        if true {
                            Console.print("yes")
                        } else {
                            Console.print("no")
                        }
                    }
                }
                """),
                Named.of("Compound assignment", """
                package demo
                class App {
                    entry(args: [Text]): void {
                        var x: Int = 1
                        x += 2
                    }
                }
                """)
        );
    }

    /**
     * Provides invalid code snippets that must throw ParseException.
     */
    static Stream<Named<String>> invalidPrograms() {
        return Stream.of(
                Named.of("Missing package", """
                class App { }
                """),
                Named.of("Bad entry signature", """
                package demo
                class App {
                    entry(): Int {
                        return 42
                    }
                }
                """),
                Named.of("Variable without initializer in method", """
                package demo
                class App {
                    entry(args: [Text]): void {
                        var x: Int
                    }
                }
                """),
                Named.of("Method in interface with body", """
                package demo
                interface Greeter {
                    greet(name: Text): void {
                        Console.print(name)
                    }
                }
                """)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validPrograms")
    @DisplayName("Valid programs should parse successfully")
    void validProgramsShouldParse(String source) {
        assertDoesNotThrow(() -> parse(source));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPrograms")
    @DisplayName("Invalid programs should throw ParseException")
    void invalidProgramsShouldFail(String source) {
        assertThrows(ParseException.class, () -> parse(source));
    }
}

