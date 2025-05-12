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
