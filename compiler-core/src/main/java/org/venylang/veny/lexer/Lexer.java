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

import org.venylang.veny.context.ParseContext;
import org.venylang.veny.util.source.Offset;
import org.venylang.veny.util.source.SrcFilePosMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A lexical analyzer that converts a source string into a list of {@link Token} objects.
 * <p>
 * This lexer uses character offsets (via {@link Offset}) instead of line numbers
 * to track token positions, enabling integration with {@link SrcFilePosMap} and file sets.
 * <p>
 * The lexer recognizes keywords, identifiers, literals, operators, and punctuation tokens,
 * and produces a final EOF token to mark the end of input.
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final SrcFilePosMap srcPosMap;
    private int start = 0;
    private int current = 0;

    private static final Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("class", TokenType.CLASS),
            Map.entry("var", TokenType.VAR),
            Map.entry("val", TokenType.VAL),
            Map.entry("pub", TokenType.PUB),
            Map.entry("pri", TokenType.PRI),
            Map.entry("return", TokenType.RETURN),
            Map.entry("interface", TokenType.INTERFACE),
            Map.entry("ext", TokenType.EXT),
            Map.entry("impl", TokenType.IMPL),
            Map.entry("if", TokenType.IF),
            Map.entry("else", TokenType.ELSE),
            Map.entry("for", TokenType.FOR),
            Map.entry("in", TokenType.IN),
            Map.entry("while", TokenType.WHILE),
            Map.entry("break", TokenType.BREAK),
            Map.entry("continue", TokenType.CONTINUE),
            Map.entry("import", TokenType.IMPORT),
            Map.entry("package", TokenType.PACKAGE),
            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("null", TokenType.NULL)
    );

    /**
     * Creates a new {@link Lexer} instance using the provided {@link ParseContext}.
     * <p>
     * This factory method extracts the source code and source position mapping from the context,
     * ensuring that both are non-null. It uses them to construct a lexer that is
     * properly configured to tokenize a single source file with accurate position tracking.
     * </p>
     *
     * @param context the {@link ParseContext} containing the source code and associated position map.
     * @return a new {@link Lexer} instance configured with the given context.
     * @throws NullPointerException if the context, source, or source position map is {@code null}.
     */
    public static Lexer of(ParseContext context) {
        Objects.requireNonNull(context, "context must not be null");
        String src = Objects.requireNonNull(context.source(), "source must not be null");
        SrcFilePosMap map = Objects.requireNonNull(context.srcFilePosMap());
        return new Lexer(src, map);
    }

    /**
     * Creates a new lexer for the given source code and position map.
     *
     * @param source     the source code to tokenize
     * @param srcPosMap  the file position map used to resolve offsets
     */
    public Lexer(String source, SrcFilePosMap srcPosMap) {
        this.source = source;
        this.srcPosMap = srcPosMap;
    }

    /**
     * Scans the entire input source and returns a list of tokens.
     * <p>
     * Each token is associated with an {@link Offset} based on its position
     * in the original source file. A final EOF token is added to the list.
     *
     * @return the list of scanned tokens
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        // EOF token should have offset at the current position
        Offset eofOffset = new Offset(srcPosMap.base() + current);
        tokens.add(new Token(TokenType.EOF, "", eofOffset));

        return tokens;
    }

    /**
     * Scans the next token from the source input starting at the current character.
     * <p>
     * This method performs a single-step dispatch based on the current character,
     * identifying simple tokens (like punctuation and operators), composite tokens
     * (like `==`, `<=`), and literals (like strings, numbers, identifiers).
     * <p>
     * Whitespace and comments are ignored. For string literals and multi-character
     * tokens, this method may delegate to helpers like {@code string()}, {@code number()},
     * or {@code identifier()}.
     * <p>
     * Tokens are recorded using character offsets into the {@link SrcFilePosMap},
     * allowing precise source location tracking independent of line numbers.
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LPAREN); break;
            case ')': addToken(TokenType.RPAREN); break;
            case '{': addToken(TokenType.LBRACE); break;
            case '}': addToken(TokenType.RBRACE); break;
            case '[': addToken(TokenType.LBRACKET); break;
            case ']': addToken(TokenType.RBRACKET); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ':': addToken(TokenType.COLON); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;

            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance(); // skip comment
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case '=': addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN); break;
            case '!': addToken(match('=') ? TokenType.NEQ : null); break;
            case '<': addToken(match('=') ? TokenType.LE : TokenType.LT); break;
            case '>': addToken(match('=') ? TokenType.GE : TokenType.GT); break;

            case '"': string(); break;

            case ' ':
            case '\r':
            case '\t':
            case '\n':
                // Whitespace: ignore (you no longer need to track line++)
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                }
                break;
        }
    }


    /**
     * Scans an identifier or keyword from the source.
     * Continues reading alphanumeric characters and underscores,
     * and checks if the resulting text matches any known keyword.
     * <p>
     * Adds either a {@code TokenType.IDENTIFIER} or the appropriate keyword token.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    /**
     * Scans an integer or floating-point number from the source.
     * Supports optional decimal points. Adds either an
     * {@code INT_LITERAL} or {@code FLOAT_LITERAL} token.
     */
    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume dot
            while (isDigit(peek())) advance();
            addToken(TokenType.FLOAT_LITERAL);
        } else {
            addToken(TokenType.INT_LITERAL);
        }
    }

    /**
     * Scans a string literal from the source.
     * Supports multi-character string values enclosed in double quotes.
     * If the string is unterminated, prints an error with the correct offset.
     * <p>
     * The resulting token does not include the surrounding quotes.
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            advance(); // No need to count lines anymore
        }

        if (isAtEnd()) {
            //Offset errorOffset = new Offset(srcPosMap.base() + start);
            //System.err.println("Unterminated string at position " + errorOffset.offset());
            addToken(TokenType.ERROR, "Unterminated string at position ");
            return;
        }

        advance(); // closing "
        String value = source.substring(start + 1, current - 1); // Exclude quotes

        Offset tokenOffset = new Offset(srcPosMap.base() + start);
        tokens.add(new Token(TokenType.TEST_LITERAL, value, tokenOffset));
    }

    /**
     * Attempts to consume the next character if it matches the expected one.
     *
     * @param expected the character to match
     * @return {@code true} if the character matched and was consumed, {@code false} otherwise
     */
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    /**
     * Peeks at the current character without consuming it.
     *
     * @return the current character, or {@code '\0'} if at end of input
     */
    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    /**
     * Peeks at the next character (lookahead by one) without consuming it.
     *
     * @return the next character, or {@code '\0'} if at or past end of input
     */
    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    /**
     * Checks whether a character is an alphabetic letter or underscore.
     *
     * @param c the character to check
     * @return {@code true} if the character is a letter or underscore
     */
    private boolean isAlpha(char c) {
        return Character.isLetter(c) || c == '_';
    }

    /**
     * Checks whether a character is a letter, digit, or underscore.
     *
     * @param c the character to check
     * @return {@code true} if the character is alphanumeric or underscore
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks whether a character is a digit.
     *
     * @param c the character to check
     * @return {@code true} if the character is a digit
     */
    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    /**
     * Checks whether the lexer has reached the end of the source input.
     *
     * @return {@code true} if all characters have been consumed
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes the current character and advances the cursor.
     *
     * @return the character that was consumed
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Adds a token of the given type using the current lexeme range.
     *
     * @param type the type of token to add
     */
    private void addToken(TokenType type) {
        addToken(type, source.substring(start, current));
    }

    /**
     * Adds a token of the specified type and lexeme at the current source position.
     * <p>
     * Computes the {@link Offset} of the token based on the start index and the
     * associated {@link SrcFilePosMap}. If the token type is {@code null}, the token
     * is ignored (e.g., for invalid characters like a lone '!').
     *
     * @param type   the type of the token (may be {@code null} to indicate an invalid or ignored token)
     * @param lexeme the exact string of characters that make up the token
     */
    private void addToken(TokenType type, String lexeme) {
        if (type == null) {
            return; // for things like `!` which are invalid alone
        }
        Offset offset = new Offset(srcPosMap.base() + start);
        tokens.add(new Token(type, lexeme, offset));
    }

}
