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
