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

import org.junit.jupiter.api.Test;
import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ast.*;
import org.venylang.veny.parser.ast.expression.AssignExpr;
import org.venylang.veny.parser.ast.statement.ExprStmt;
import org.venylang.veny.parser.ast.statement.IfStmt;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for {@link RecursiveDescentParser}.
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
class RecursiveDescentParserTest {

    private VenyFile parse(String source) {
        // Build the SrcFilePosMap from the source
        SrcFileSet fileSet = new SrcFileSet();
        SrcFilePosMap posMap = new SrcFilePosMap(fileSet, "test", 1, source.length(), new int[]{0});

        // Run the lexer
        Lexer lexer = new Lexer(source, posMap);
        List<Token> tokens = lexer.scanTokens();

        // Build the ParseContext correctly
        ParseContext ctx = ParseContext.builder()
                .source(source)                      // ✅ actual source
                .filePath(Path.of("test.veny"))      // ✅ fake file path for tests
                .srcFilePosMap(posMap)               // ✅ correct position map
                .build();

        // Parse into AST
        return new RecursiveDescentParser(tokens, ctx).parse();
    }

    @Test
    void testPackageDeclarationRequired() {
        String src = "package demo class Foo {}";
        VenyFile file = parse(src);
        assertEquals("demo", file.packageName());
        assertEquals(1, file.classes().size());
    }

    @Test
    void testMissingPackageThrows() {
        String src = "class Foo {}";
        assertThrows(ParseException.class, () -> parse(src));
    }

    @Test
    void testImports() {
        String src = """
            package demo
            import foo.bar
            import baz.qux
            class Foo {}
            """;
        VenyFile file = parse(src);
        assertEquals(List.of("foo.bar", "baz.qux"), file.imports());
    }

    @Test
    void testClassWithFieldAndMethod() {
        String src = """
            package demo
            class Foo {
                val x: Int
                pub entry(args: [Text]): void {
                    return 1
                }
            }
            """;
        VenyFile file = parse(src);
        ClassDecl clazz = file.classes().get(0);

        assertEquals("Foo", clazz.name());
        assertEquals(1, clazz.fields().size());
        assertEquals(1, clazz.methods().size());
        assertEquals("entry", clazz.methods().get(0).name());
    }

    @Test
    void testInterfaceWithMethod() {
        String src = """
            package demo
            interface Bar {
                foo(x: Int): void
            }
            """;
        VenyFile file = parse(src);
        InterfaceDecl iface = file.interfaces().get(0);

        assertEquals("Bar", iface.name());
        assertEquals("foo", iface.methods().get(0).name());
    }

    @Test
    void testIfElseStatement() {
        String src = """
            package demo
            class Foo {
                bar(): void {
                    if true { return 1 } else { return 2 }
                }
            }
            """;
        VenyFile file = parse(src);
        MethodDecl method = file.classes().get(0).methods().get(0);
        assertFalse(method.body().isEmpty());
        assertTrue(method.body().get(0) instanceof IfStmt);
    }

    @Test
    void testCompoundAssignment() {
        String src = """
            package demo
            class Foo {
                bar(): void {
                    var x: Int = 1
                    x += 2
                }
            }
            """;
        VenyFile file = parse(src);
        MethodDecl method = file.classes().get(0).methods().get(0);

        // The second statement should be an expression statement with an assignment
        Statement stmt = method.body().get(1);
        assertTrue(stmt instanceof ExprStmt);
        Expression expr = ((ExprStmt) stmt).expression();
        assertTrue(expr instanceof AssignExpr);
    }

    @Test
    void testInvalidEntrySignatureThrows() {
        String src = """
            package demo
            class Foo {
                entry(): void {}
            }
            """;
        assertThrows(ParseException.class, () -> parse(src));
    }
}