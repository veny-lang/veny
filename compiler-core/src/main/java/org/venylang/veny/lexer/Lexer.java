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

        Offset eofOffset = new Offset(srcPosMap.base() + current);
        tokens.add(new Token(TokenType.EOF, "", null, eofOffset));
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
            case '(' -> addToken(TokenType.LPAREN);
            case ')' -> addToken(TokenType.RPAREN);
            case '{' -> addToken(TokenType.LBRACE);
            case '}' -> addToken(TokenType.RBRACE);
            case '[' -> addToken(TokenType.LBRACKET);
            case ']' -> addToken(TokenType.RBRACKET);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> {
                if (match('.')) addToken(TokenType.DOUBLE_DOT); else addToken(TokenType.DOT);
            }
            case ';' -> addToken(TokenType.SEMICOLON);
            case ':' -> {
                if (match(':')) addToken(TokenType.DOUBLE_COLON);
                else if (match('=')) addToken(TokenType.COLON_EQ);
                else addToken(TokenType.COLON);
            }
            case '+' -> {
                if (match('+')) addToken(TokenType.INCREMENT);
                else if (match('=')) addToken(TokenType.PLUS_EQ);
                else addToken(TokenType.PLUS);
            }
            case '-' -> {
                if (match('-')) addToken(TokenType.DECREMENT);
                else if (match('>')) addToken(TokenType.ARROW);
                else if (match('=')) addToken(TokenType.MINUS_EQ);
                else addToken(TokenType.MINUS);
            }
            case '*' -> {
                if (match('=')) addToken(TokenType.STAR_EQ);
                else addToken(TokenType.STAR);
            }
            case '/' -> {
                if (match('/')) {
                    // single-line comment: consume until newline or EOF
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('=')) {
                    addToken(TokenType.SLASH_EQ);
                } else {
                    addToken(TokenType.SLASH);
                }
            }
            case '%' -> {
                if (match('=')) addToken(TokenType.MOD_EQ); else addToken(TokenType.MOD);
            }

            case '=' -> {
                if (match('=')) addToken(TokenType.EQ);
                else if (match('>')) addToken(TokenType.DOUBLE_ARROW);
                else addToken(TokenType.ASSIGN);
            }

            case '!' -> addToken(match('=') ? TokenType.NEQ : TokenType.BANG);

            case '<' -> {
                if (match('=')) addToken(TokenType.LE);
                else if (match('<')) addToken(TokenType.LT_LT);
                else addToken(TokenType.LT);
            }
            case '>' -> {
                if (match('=')) addToken(TokenType.GE);
                else if (match('>')) addToken(TokenType.GT_GT);
                else addToken(TokenType.GT);
            }

            case '&' -> addToken(match('&') ? TokenType.AND : TokenType.AMPERSAND);
            case '|' -> addToken(match('|') ? TokenType.OR : TokenType.PIPE);
            case '^' -> addToken(TokenType.CARET);
            case '~' -> addToken(TokenType.TILDE);
            case '?' -> addToken(TokenType.QUESTION);

            case '"' -> text();

            case ' ', '\r', '\t', '\n' -> {
                // ignore whitespace
            }

            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // unknown / unsupported char -> emit ERROR token
                    addToken(TokenType.ERROR, source.substring(start, current));
                }
            }
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
        TokenType type = keywords.get(text);

        if (type != null) {
            // Handle true/false/null as literals
            switch (type) {
                case TRUE -> addToken(TokenType.TRUE, text, Boolean.TRUE);
                case FALSE -> addToken(TokenType.FALSE, text, Boolean.FALSE);
                case NULL -> addToken(TokenType.NULL, text, null);
                default -> addToken(type, text); // keywords other than literals
            }
        } else {
            addToken(TokenType.IDENTIFIER, text, text);
        }
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
            String lexeme = source.substring(start, current);
            addToken(TokenType.FLOAT_LITERAL, lexeme, Double.parseDouble(lexeme));
        } else {
            String lexeme = source.substring(start, current);
            addToken(TokenType.INT_LITERAL, lexeme, Integer.parseInt(lexeme));
        }
    }

    /**
     * Scans a text literal from the source.
     * Supports multi-character text values enclosed in double quotes.
     * If the text is unterminated, prints an error with the correct offset.
     * <p>
     * The resulting token does not include the surrounding quotes.
     */
    private void text() {
        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        if (isAtEnd()) {
            addToken(TokenType.ERROR, "Unterminated string");
            return;
        }

        advance(); // closing quote
        String value = source.substring(start + 1, current - 1); // inner contents
        addToken(TokenType.TEXT_LITERAL, source.substring(start, current), value);
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
     * <p>
     * The lexeme is taken directly from the source substring spanning
     * the current token, and the literal value is {@code null}.
     *
     * @param type the type of token to add (must not be {@code null})
     */
    private void addToken(TokenType type) {
        addToken(type, source.substring(start, current), null);
    }

    /**
     * Adds a token of the given type with an associated literal value.
     * <p>
     * The lexeme is taken from the current source substring. The literal
     * is a parsed or computed value (e.g., {@code Integer}, {@code Double},
     * {@code String}), or {@code null} if not applicable.
     *
     * @param type    the type of token to add (must not be {@code null})
     * @param literal the parsed literal value associated with the token
     */
    private void addToken(TokenType type, Object literal) {
        addToken(type, source.substring(start, current), literal);
    }

    /**
     * Adds a token of the given type with an explicit lexeme string.
     * <p>
     * The literal value is set to {@code null}.
     *
     * @param type   the type of token to add (must not be {@code null})
     * @param lexeme the exact text matched for this token
     */
    private void addToken(TokenType type, String lexeme) {
        addToken(type, lexeme, null);
    }

    /**
     * Adds a token of the specified type, lexeme, and literal at the current source position.
     * <p>
     * The token’s {@link Offset} is computed based on the starting index
     * relative to the {@link SrcFilePosMap}.
     * <p>
     * If the token type is {@code null}, the method does nothing.
     *
     * @param type    the type of the token (may be {@code null} to ignore invalid tokens)
     * @param lexeme  the exact text of the token as it appears in the source
     * @param literal the parsed literal value (e.g., numeric value, string contents),
     *                or {@code null} if not applicable
     */
    private void addToken(TokenType type, String lexeme, Object literal) {
        if (type == null) return;
        Offset offset = new Offset(srcPosMap.base() + start);
        tokens.add(new Token(type, lexeme, literal, offset));
    }

}
