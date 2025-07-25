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

package org.venylang.veny.lexer;

import org.junit.jupiter.api.Test;
import org.venylang.veny.util.source.SrcFilePosMap;
import org.venylang.veny.util.source.SrcFileSet;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VenyLexerTest {

    private Lexer makeLexer(String source) {
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
        return new Lexer(source, posMap);
    }

    @Test
    public void testKeywords() {
        String input = "var class return";
        Lexer lexer = makeLexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.VAR, tokens.get(0).type());
        assertEquals(TokenType.CLASS, tokens.get(1).type());
        assertEquals(TokenType.RETURN, tokens.get(2).type());
    }

    @Test
    public void testIdentifiers() {
        String input = "myVar another_name";
        Lexer lexer = makeLexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
        assertEquals("myVar", tokens.get(0).lexeme());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals("another_name", tokens.get(1).lexeme());
    }

    @Test
    public void testLiterals() {
        String input = "42 3.14 \"hello\"";
        Lexer lexer = makeLexer(input);
        List<Token> tokens = lexer.scanTokens();
        assertEquals(TokenType.INT_LITERAL, tokens.get(0).type());
        assertEquals("42", tokens.get(0).lexeme());
        //assertEquals(TokenType.INT_LITERAL, tokens.get(1).type());
        assertEquals("3.14", tokens.get(1).lexeme());
        assertEquals(TokenType.TEST_LITERAL, tokens.get(2).type());
        assertEquals("hello", tokens.get(2).lexeme());
    }

    @Test
    public void testSymbols() {
        String input = "{ } ( ) : ; + - * / = == != < <= > >=";
        Lexer lexer = makeLexer(input);
        List<Token> tokens = lexer.scanTokens();
        TokenType[] expected = {
                TokenType.LBRACE, TokenType.RBRACE, TokenType.LPAREN, TokenType.RPAREN,
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
        Lexer lexer = makeLexer(input);
        lexer.scanTokens();
        //Exception exception = assertThrows(LexerException.class, lexer::scanTokens);
        //assertTrue(exception.getMessage().contains("Unterminated string"));
    }

    @Test
    public void testInvalidCharacter() {
        String input = "@";
        Lexer lexer = makeLexer(input);
        lexer.scanTokens();
        //Exception exception = assertThrows(LexerException.class, lexer::scanTokens);
        //assertTrue(exception.getMessage().contains("Unexpected character"));
    }
}
