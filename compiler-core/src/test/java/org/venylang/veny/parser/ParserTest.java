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

import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ast.*;
import org.junit.jupiter.api.Test;
import org.venylang.veny.parser.ast.expression.ArrayLiteralExpr;
import org.venylang.veny.parser.ast.expression.LiteralExpr;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void testParseProgram() {
        String input = "package test class Person { var name: String = \"John\" }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);

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

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);

        ParseException ex = assertThrows(ParseException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected '=' to initialize variable"));
    }

    @Test
    public void testArrayVariableDeclaration() {
        String source = "package test class Main { var x: [Int] = [1, 2, 3] }";

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        // Parse it
        Parser parser = new RecursiveDescentParser(tokens);
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
