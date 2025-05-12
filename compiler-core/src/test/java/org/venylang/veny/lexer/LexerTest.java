package org.venylang.veny.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    @Test
    public void testVariableDeclaration() {
        Lexer lexer = new Lexer("var x: Int = 42;");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(TokenType.VAR, tokens.get(0).type());
        assertEquals("x", tokens.get(1).lexeme());
        assertEquals(TokenType.COLON, tokens.get(2).type());
        assertEquals("Int", tokens.get(3).lexeme());
        assertEquals(TokenType.ASSIGN, tokens.get(4).type());
        assertEquals("42", tokens.get(5).lexeme());
        assertEquals(TokenType.INT_LITERAL, tokens.get(5).type());
    }

    @Test
    public void testStringLiteral() {
        Lexer lexer = new Lexer("var name: String = \"Lumina\";");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(TokenType.STRING_LITERAL, tokens.get(5).type());
        assertEquals("Lumina", tokens.get(5).lexeme());
    }

}
