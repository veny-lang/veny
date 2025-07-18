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

public enum TokenType {
    // Keywords
    CLASS, INTERFACE, EXT, IMPL, VAR, VAL, RETURN, IF, ELSE, FOR, WHILE,
    BREAK, CONTINUE, IMPORT, PACKAGE, TRUE, FALSE, NULL,
    PUB, PRI, IN,

    // Identifiers and literals
    IDENTIFIER, INT_LITERAL, FLOAT_LITERAL, TEST_LITERAL,

    // Symbols
    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /
    MOD, // %
    BANG, // !
    AMPERSAND, // &
    PIPE, // |
    CARET, // ^
    TILDE, // ~
    QUESTION, // ?
    ASSIGN, EQ, NEQ, // =, ==, !=
    AND, OR, // &&, ||
    INCREMENT, DECREMENT, // ++, --
    BANG_EQ, // !=
    LT, GT, LE, GE, // <, >, <=, >=
    LT_LT, GT_GT, // <<, >>
    PLUS_EQ, MINUS_EQ, STAR_EQ, SLASH_EQ, MOD_EQ,
    ARROW, // ->
    DOUBLE_ARROW, // =>
    COLON_EQ, // :=
    DOUBLE_COLON, // ::
    DOUBLE_DOT, // ..
    DOUBLE_PIPE, // ||
    DOUBLE_AMPERSAND, // &&
    LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,
    SEMICOLON, COLON, COMMA, DOT,

    // Special tokens
    ERROR, // Error
    EOF    // End of a file
}
