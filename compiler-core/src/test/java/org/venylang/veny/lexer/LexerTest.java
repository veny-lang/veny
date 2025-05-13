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

import java.util.Arrays;
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
        Lexer lexer = new Lexer("var name: String = \"Veny\"");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(TokenType.STRING_LITERAL, tokens.get(5).type());
        assertEquals("Veny", tokens.get(5).lexeme());
    }

    @Test
    public void testArrayDeclaration() {
        Lexer lexer = new Lexer("var x: [Int] = [1, 2, 3]");
        List<Token> actual = lexer.scanTokens();

        List<Token> expected = Arrays.asList(
                new Token(TokenType.VAR, "var", 1),
                new Token(TokenType.IDENTIFIER, "x", 1),
                new Token(TokenType.COLON, ":", 1),
                new Token(TokenType.LBRACKET, "[", 1),
                new Token(TokenType.IDENTIFIER, "Int", 1),
                new Token(TokenType.RBRACKET, "]", 1),
                new Token(TokenType.ASSIGN, "=", 1),
                new Token(TokenType.LBRACKET, "[", 1),
                new Token(TokenType.INT_LITERAL, "1", 1),
                new Token(TokenType.COMMA, ",", 1),
                new Token(TokenType.INT_LITERAL, "2", 1),
                new Token(TokenType.COMMA, ",", 1),
                new Token(TokenType.INT_LITERAL, "3", 1),
                new Token(TokenType.RBRACKET, "]", 1),
                new Token(TokenType.EOF, "", 1)
        );

        assertEquals(expected, actual);
    }

}
