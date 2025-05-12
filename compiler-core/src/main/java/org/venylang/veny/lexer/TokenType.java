package org.venylang.veny.lexer;

public enum TokenType {
    // Keywords
    CLASS, VAR, VAL, RETURN, IF, ELSE, FOR, WHILE,
    BREAK, CONTINUE, IMPORT, PACKAGE, TRUE, FALSE, NULL,
    PUB, PRI, IN,

    // Identifiers and literals
    IDENTIFIER, INT_LITERAL, FLOAT_LITERAL, STRING_LITERAL,

    // Symbols
    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH,
    ASSIGN, EQ, NEQ,
    LT, GT, LE, GE,
    OPEN_PAREN, CLOSE_PAREN, OPEN_BRACE, CLOSE_BRACE,
    SEMICOLON, COLON, COMMA, DOT,

    // End of a file
    EOF
}
