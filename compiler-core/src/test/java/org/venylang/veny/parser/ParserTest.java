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

import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ast.*;
import org.junit.jupiter.api.Test;
import org.venylang.veny.parser.ast.expression.ArrayLiteralExpr;
import org.venylang.veny.parser.ast.expression.LiteralExpr;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

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

    @Test
    void testParseProgram() {
        String input = "package test class Person { var name: String = \"John\" }";
        ParseContext context = makeParseContext(input);
        Lexer lexer = makeLexer(context);
        List<Token> tokens = lexer.scanTokens();

        RecursiveDescentParser parser = new RecursiveDescentParser(tokens, context);

        VenyFile program = parser.parse();
        assertNotNull(program);
        assertEquals(1, program.classes().size());
        assertEquals("Person", program.classes().get(0).name());
    }

    @Test
    void testVariableDeclarationRequiresInitializer() {
        String source = """
            package org.venylang.veny.test
            
            class Test {
                var x: Int
            }
        """;

        ParseContext context = makeParseContext(source);
        Lexer lexer = makeLexer(context);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens, context);

        ParseException ex = assertThrows(ParseException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected '=' to initialize variable"));
    }

    @Test
    public void testArrayVariableDeclaration() {
        String source = "package test class Main { var x: [Int] = [1, 2, 3] }";
        ParseContext context = makeParseContext(source);

        Lexer lexer = makeLexer(context);
        List<Token> tokens = lexer.scanTokens();

        // Parse it
        Parser parser = new RecursiveDescentParser(tokens, context);
        VenyFile program = parser.parse();

        // Assertions
        assertNotNull(program);
        assertEquals(1, program.classes().size());

        ClassDecl classDecl = program.classes().get(0);
        assertEquals("Main", classDecl.name());
        assertEquals(1, classDecl.fields().size());

        VarDecl varDecl = classDecl.fields().get(0);
        assertEquals("x", varDecl.name());
        assertEquals("[Int]", varDecl.typeName());
        assertTrue(varDecl.initializer() instanceof ArrayLiteralExpr);

        ArrayLiteralExpr arrayLiteral = (ArrayLiteralExpr) varDecl.initializer();
        List<Expression> elements = arrayLiteral.elements();

        assertEquals(3, elements.size());
        assertTrue(elements.get(0) instanceof LiteralExpr);
        assertEquals(1, ((LiteralExpr) elements.get(0)).value());
        assertEquals(2, ((LiteralExpr) elements.get(1)).value());
        assertEquals(3, ((LiteralExpr) elements.get(2)).value());
    }

}
