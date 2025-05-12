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
import org.venylang.veny.parser.ast.Program;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void testParseProgram() {
        String input = "class Person { var name: String = \"John\" }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);

        Program program = parser.parse();
        assertNotNull(program);
        assertEquals(1, program.classes().size());
        assertEquals("Person", program.classes().get(0).name());
    }

    @Test
    void testVariableDeclarationRequiresInitializer() {
        String source = """
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


}
