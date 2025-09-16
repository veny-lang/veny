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

class LexerTest {

    // Helper method to create a Lexer using your previous setup
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

    // Convenience method to tokenize a string
    private List<Token> lex(String src) {
        return lexer(src).scanTokens();
    }

    @Test
    void testKeywords() {
        String src = "class var val pub pri return interface ext impl if else for in while break continue import package true false null";
        List<Token> tokens = lex(src);

        TokenType[] expected = {
                TokenType.CLASS, TokenType.VAR, TokenType.VAL, TokenType.PUB, TokenType.PRI,
                TokenType.RETURN, TokenType.INTERFACE, TokenType.EXT, TokenType.IMPL,
                TokenType.IF, TokenType.ELSE, TokenType.FOR, TokenType.IN, TokenType.WHILE,
                TokenType.BREAK, TokenType.CONTINUE, TokenType.IMPORT, TokenType.PACKAGE,
                TokenType.TRUE, TokenType.FALSE, TokenType.NULL, TokenType.EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testIdentifiers() {
        String src = "foo bar _baz A1";
        List<Token> tokens = lex(src);
        assertEquals(5, tokens.size()); // 4 identifiers + EOF
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
        assertEquals("foo", tokens.get(0).literal());
        assertEquals("bar", tokens.get(1).literal());
        assertEquals("_baz", tokens.get(2).literal());
        assertEquals("A1", tokens.get(3).literal());
    }

    @Test
    void testNumbers() {
        String src = "123 0 42 3.14 0.0";
        List<Token> tokens = lex(src);

        assertEquals(TokenType.INT_LITERAL, tokens.get(0).type());
        assertEquals(123, tokens.get(0).literal());

        assertEquals(TokenType.INT_LITERAL, tokens.get(1).type());
        assertEquals(0, tokens.get(1).literal());

        assertEquals(TokenType.INT_LITERAL, tokens.get(2).type());
        assertEquals(42, tokens.get(2).literal());

        assertEquals(TokenType.FLOAT_LITERAL, tokens.get(3).type());
        assertEquals(3.14, tokens.get(3).literal());

        assertEquals(TokenType.FLOAT_LITERAL, tokens.get(4).type());
        assertEquals(0.0, tokens.get(4).literal());
    }

    @Test
    void testTextLiterals() {
        String src = "\"Hello\" \"World\" \"\"";
        List<Token> tokens = lex(src);

        assertEquals("Hello", tokens.get(0).literal());
        assertEquals("World", tokens.get(1).literal());
        assertEquals("", tokens.get(2).literal());
    }

    @Test
    void testOperatorsAndPunctuation() {
        String src = "+ - * / % = == != < > <= >= && || ! ++ -- -> => := :: .. ; , : ( ) { } [ ]";
        List<Token> tokens = lex(src);

        TokenType[] expected = {
                TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.SLASH, TokenType.MOD,
                TokenType.ASSIGN, TokenType.EQ, TokenType.NEQ, TokenType.LT, TokenType.GT,
                TokenType.LE, TokenType.GE, TokenType.AND, TokenType.OR, TokenType.BANG,
                TokenType.INCREMENT, TokenType.DECREMENT, TokenType.ARROW, TokenType.DOUBLE_ARROW,
                TokenType.COLON_EQ, TokenType.DOUBLE_COLON, TokenType.DOUBLE_DOT,
                TokenType.SEMICOLON, TokenType.COMMA, TokenType.COLON,
                TokenType.LPAREN, TokenType.RPAREN, TokenType.LBRACE, TokenType.RBRACE,
                TokenType.LBRACKET, TokenType.RBRACKET, TokenType.EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testCommentsAndWhitespace() {
        String src = "var x = 1 // this is a comment\nval y = 2";
        List<Token> tokens = lex(src);

        assertEquals(TokenType.VAR, tokens.get(0).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals(TokenType.ASSIGN, tokens.get(2).type());
        assertEquals(TokenType.INT_LITERAL, tokens.get(3).type());

        assertEquals(TokenType.VAL, tokens.get(4).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(5).type());
        assertEquals(TokenType.ASSIGN, tokens.get(6).type());
        assertEquals(TokenType.INT_LITERAL, tokens.get(7).type());
    }
}
