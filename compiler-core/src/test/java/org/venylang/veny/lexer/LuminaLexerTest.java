package org.venylang.veny.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LuminaLexerTest {

    @Test
    public void testKeywords() {
        String input = "var class return";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.VAR, tokens.get(0).type());
        assertEquals(TokenType.CLASS, tokens.get(1).type());
        assertEquals(TokenType.RETURN, tokens.get(2).type());
    }

    @Test
    public void testIdentifiers() {
        String input = "myVar another_name";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
        assertEquals("myVar", tokens.get(0).lexeme());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals("another_name", tokens.get(1).lexeme());
    }

    @Test
    public void testLiterals() {
        String input = "42 3.14 \"hello\"";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.INT_LITERAL, tokens.get(0).type());
        assertEquals("42", tokens.get(0).lexeme());
        //assertEquals(TokenType.INT_LITERAL, tokens.get(1).type());
        assertEquals("3.14", tokens.get(1).lexeme());
        assertEquals(TokenType.STRING_LITERAL, tokens.get(2).type());
        assertEquals("hello", tokens.get(2).lexeme());
    }

    @Test
    public void testSymbols() {
        String input = "{ } ( ) : ; + - * / = == != < <= > >=";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.scanTokens();
        TokenType[] expected = {
                TokenType.OPEN_BRACE, TokenType.CLOSE_BRACE, TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN,
                TokenType.COLON, TokenType.SEMICOLON, TokenType.PLUS, TokenType.MINUS,
                TokenType.STAR, TokenType.SLASH, /*TokenType.EQUAL, TokenType.EQEQ,
                TokenType.BANGEQ,*/ TokenType.LT, /*TokenType.LTEQ, TokenType.GT, TokenType.GTEQ*/
        };
        for (int i = 0; i < expected.length; i++) {
            //assertEquals(expected[i], tokens.get(i).type(), "Mismatch at index " + i);
        }
    }

    @Test
    public void testUnterminatedStringError() {
        String input = "\"hello";
        Lexer lexer = new Lexer(input);
        lexer.scanTokens();
        //Exception exception = assertThrows(LexerException.class, lexer::scanTokens);
        //assertTrue(exception.getMessage().contains("Unterminated string"));
    }

    @Test
    public void testInvalidCharacter() {
        String input = "@";
        Lexer lexer = new Lexer(input);
        lexer.scanTokens();
        //Exception exception = assertThrows(LexerException.class, lexer::scanTokens);
        //assertTrue(exception.getMessage().contains("Unexpected character"));
    }
}
