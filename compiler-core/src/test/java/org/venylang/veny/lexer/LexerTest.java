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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {


    private Lexer lexer(String source) {
        SrcFileSet fileSet = new SrcFileSet();

        // Simple line start index calculator
        List<Integer> lineOffsets = new ArrayList<>();
        lineOffsets.add(0); // First line always starts at 0
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '\n') {
                lineOffsets.add(i + 1);
            }
        }
        int[] lines = lineOffsets.stream().mapToInt(Integer::intValue).toArray();

        int base = 1;
        int size = source.length();

        SrcFilePosMap posMap = new SrcFilePosMap(fileSet, "test", base, size, lines);
        return new Lexer(source, posMap);
    }

    @Test
    public void testVariableDeclaration() {
        Lexer lexer = lexer("var x: Int = 42;");
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
        Lexer lexer = lexer("var name: String = \"Veny\"");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(TokenType.TEST_LITERAL, tokens.get(5).type());
        assertEquals("Veny", tokens.get(5).lexeme());
    }

    @Test
    public void testArrayDeclaration() {
        Lexer lexer = lexer("var x: [Int] = [1, 2, 3]");
        List<Token> actual = lexer.scanTokens();

        // Just compare the token types and lexemes, not offsets
        List<TokenType> expectedTypes = Arrays.asList(
                TokenType.VAR,
                TokenType.IDENTIFIER,
                TokenType.COLON,
                TokenType.LBRACKET,
                TokenType.IDENTIFIER,
                TokenType.RBRACKET,
                TokenType.ASSIGN,
                TokenType.LBRACKET,
                TokenType.INT_LITERAL,
                TokenType.COMMA,
                TokenType.INT_LITERAL,
                TokenType.COMMA,
                TokenType.INT_LITERAL,
                TokenType.RBRACKET,
                TokenType.EOF
        );

        List<String> expectedLexemes = Arrays.asList(
                "var", "x", ":", "[", "Int", "]", "=", "[", "1", ",", "2", ",", "3", "]", ""
        );

        for (int i = 0; i < expectedTypes.size(); i++) {
            assertEquals(expectedTypes.get(i), actual.get(i).type(), "Unexpected token type at index " + i);
            assertEquals(expectedLexemes.get(i), actual.get(i).lexeme(), "Unexpected lexeme at index " + i);
        }
    }

}
